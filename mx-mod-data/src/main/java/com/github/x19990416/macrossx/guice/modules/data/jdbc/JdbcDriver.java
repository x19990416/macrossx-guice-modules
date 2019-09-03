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
import com.github.x19990416.macrossx.guice.modules.data.Page;
import com.google.common.collect.Lists;
import lombok.Data;

@Data
public class JdbcDriver implements IMxJdbcDriver {
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
  
  private <T> List<T> convert(ResultSetMetaData md,ResultSet rs,int fetchRows,Class<T> clazz){
    List<T> result = Lists.newArrayList();    
    try {
      while(rs.next()) {
        T target = clazz.getDeclaredConstructor().newInstance();
        for(int i =1; i<=md.getColumnCount();i++) {
          BeanUtils.setProperty(target, md.getColumnLabel(i), rs.getObject(i));
        }
        result.add(target);
        if(fetchRows>0) {
          fetchRows = fetchRows -1;
          System.out.println("row>>>>\t"+fetchRows);
          if(fetchRows<=0)break;
        }
      }
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException | SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return result;
  }
  
  public <T> List<T> find(String sql,Object[] values,Class<T> clazz) throws SQLException {
    PreparedStatement statement = null;
    List<T> result = Lists.newArrayList();
    try {
      this.connect();
      statement = this.execute(sql, values, null);
      result = this.convert(statement.getMetaData(),statement.getResultSet(),0,clazz);
      statement.closeOnCompletion();
    }finally {
      if(statement!=null && !statement.isClosed()) {
        statement.closeOnCompletion();
      }
      this.disconnect();
    }
    return result;
  }
  
  public <T> Page<T> find(String sql,Object[] values,int pageSize,int pageNum,Class<T> clazz) throws SQLException {
    PreparedStatement statement = null;
    Page<T> page = new Page<T>();
    try {
      this.connect();
      statement = this.execute(sql, values, null);
      ResultSet rs = statement.getResultSet();
      rs.absolute(pageSize*pageNum);
      List<T> rows = this.convert(statement.getMetaData(),rs,pageSize,clazz);
      statement.closeOnCompletion();
      page.setContents(rows);
      page.setPageNum(pageNum);
      page.setPageSize(pageSize);
      rs.last();
      page.setTotal(rs.getRow());
    }finally {
      if(statement!=null && !statement.isClosed()) {
        statement.closeOnCompletion();
      }
      this.disconnect();
    }
    System.out.println(page.getContents().size());
    return page;
  }
  
  private PreparedStatement execute(String sql, Object[] values,Integer maxRows) throws SQLException {
    conActive.setAutoCommit(false);
    PreparedStatement stmtActive =  this.conActive.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
    for (int i = 1 ; i<=values.length;i++) {
      System.out.println("xxxxxxxxxxx\t"+values[i-1]);
      stmtActive.setObject(i, values[i-1]);
    }
    if(maxRows != null && maxRows>0) {
      stmtActive.setMaxRows(maxRows);
    }
    stmtActive.execute();
    conActive.commit();
    return stmtActive;
  }
  

  @Override
  public int saveOrUpdate(String sql, String... values) throws SQLException {
    PreparedStatement statement = null;
    int ret = -1;
    try {
      this.connect();
      statement =  this.execute(sql, values, null);
      ResultSet rs = statement.getGeneratedKeys();
      if(rs.next()) {
        ret = rs.getInt(1);
      } else {
       ret = statement.getUpdateCount();
      } 
    }finally {
     if(statement!=null && !statement.isClosed()) {
       statement.closeOnCompletion();
     }
     this.disconnect();
     }
    return ret;
  }
  
//  public  int saveOrUpdate(String sql,Object ...values) throws SQLException {
//    Object[] results = this.execute(sql, values,null);
//    return (int)results[0];
//  }
   
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
