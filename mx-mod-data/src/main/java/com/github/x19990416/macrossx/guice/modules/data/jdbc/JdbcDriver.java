/*
 * Copyright (C) 2019 The mx-mod-jdbc Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.x19990416.macrossx.guice.modules.data.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.beanutils.BeanUtils;
import com.github.x19990416.macrossx.guice.modules.common.Constants;
import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class JdbcDriver {
  @Inject
  @Named(Constants.MX_JDBC_URL)
  private String jdbcUrl;
  @Inject
  @Named(Constants.MX_JDBC_DRIVER)
  private String jdbcDriver;
  @Inject
  @Named(Constants.MX_JDBC_USR)
  private String jdbcUser;
  @Inject
  @Named(Constants.MX_JDBC_PWD)
  private String jdbcPassword;

  private Connection conActive; 

  public void connect() {
    try {
      if (conActive == null) {
        Class.forName(jdbcDriver);
        conActive = DriverManager.getConnection(jdbcUrl);
      }
      else if(conActive.isClosed()) {
        conActive = DriverManager.getConnection(jdbcUrl);
      }
    } catch (SQLException | ClassNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public <T> List<T> find(String sql,String[] values,Class<T> clazz) throws SQLException {
    List<T> result = Lists.newArrayList();
    Object[] results = this.execute(sql, values);
    List<Object[]> rowDatas = (List<Object[]>)results[1];
    rowDatas.forEach(data->{
      try {
        T target = clazz.getDeclaredConstructor().newInstance();
        for(int i=0;i<data.length;i++) {
         BeanUtils.setProperty(target, (String)((Object[])data[i])[0], ((Object[])data[i])[1]);
        }
        result.add(target);
      } catch (IllegalAccessException | InvocationTargetException | InstantiationException | IllegalArgumentException | NoSuchMethodException | SecurityException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });
    return result;
  }
  
  private Object[] execute(String sql, String ...values) throws SQLException {
    PreparedStatement stmtActive = null;
    Object[] ret = new Object[2];
    try {
    this.connect();
    conActive.setAutoCommit(false);
    stmtActive =  this.conActive.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
    for (int i = 1 ; i<=values.length;i++) {
      stmtActive.setString(i, values[i-1]);
    }
    stmtActive.execute();
    conActive.commit();
    ResultSet rs = stmtActive.getGeneratedKeys();
    if(rs.next()) {
      ret[0] = rs.getInt(1);
    } else {
      ret[0] = stmtActive.getUpdateCount();
    }
    //获取数据,key,value,type
    rs = stmtActive.getResultSet(); 
    if(rs!=null) {
    ResultSetMetaData md = rs.getMetaData();
    List<Object[]> rowDatas = Lists.newArrayList();
    while (rs.next()) {
      Object[] columns = new Object[md.getColumnCount()];
      for (int i = 1; i <= md.getColumnCount(); i++) {
        Object[] column = new Object[3];//声明Map
        column[0] = md.getColumnName(i);
        column[1] = rs.getObject(i);
        column[2] = md.getColumnClassName(i);
        columns[i-1] = column; 
      }
      rowDatas.add(columns);
    }
    ret[1] = rowDatas;
    }}finally {
      if(stmtActive!=null &&!stmtActive.isClosed()) {
        stmtActive.closeOnCompletion();
      }
    }
    return ret;
  }
  
  public  int saveOrUpdate(String sql,String ...values) throws ClassNotFoundException, SQLException {
    Object[] results = this.execute(sql, values);
    return (int)results[0];
  }
   
  public void disconnect() {
    // 关闭数据库连接
    try {
      if(conActive!=null && !conActive.isClosed())
        conActive.close();
    } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
  
  protected void finalize() {
      this.disconnect();
  }
}
