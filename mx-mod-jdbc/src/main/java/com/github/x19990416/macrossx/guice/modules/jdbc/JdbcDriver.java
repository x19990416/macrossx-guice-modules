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
package com.github.x19990416.macrossx.guice.modules.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.inject.Inject;
import javax.inject.Named;
import com.github.x19990416.macrossx.guice.modules.common.Constants;
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

  private boolean hasResult = false;
  
  private Connection conActive; 

  public void connect() throws ClassNotFoundException {
    try {
      if (conActive == null) {
        Class.forName(jdbcDriver);
        conActive = DriverManager.getConnection(jdbcUrl);
        hasResult = false;
      }
      else if(conActive.isClosed()) {
        conActive = DriverManager.getConnection(jdbcUrl);
        hasResult = false;
      }
    } catch (SQLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  public  int saveOrUpdate(String sql,String ...values) throws ClassNotFoundException, SQLException {
    this.connect();
    PreparedStatement stmtActive = null ;
    try {
      conActive.setAutoCommit(false);
      stmtActive =  this.conActive.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
      for (int i = 1 ; i<=values.length;i++) {
        stmtActive.setString(i, values[i-1]);
      }
      int rows = stmtActive.executeUpdate();
      conActive.commit();
      ResultSet rs1 = stmtActive.getGeneratedKeys();
      if(rs1.next()) {
        rows = rs1.getInt(1);
      }
      return rows;
    }finally {
      if(stmtActive !=null) {
        stmtActive.close();
      }
      this.disconnect();
    }

  }
   
  public void disconnect() {
    // 关闭数据库连接
    try {
        conActive.close();
    } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }
}
}
