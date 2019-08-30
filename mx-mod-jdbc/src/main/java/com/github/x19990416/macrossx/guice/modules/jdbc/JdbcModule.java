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

import java.sql.SQLException;
import javax.inject.Inject;
import com.github.x19990416.macrossx.guice.modules.common.BaseModule;
import com.github.x19990416.macrossx.guice.modules.configuration.ConfigurationModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class JdbcModule extends BaseModule {

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "JDBCModule";
  }

  @Override
  public AbstractModule export(String key) {
    // TODO Auto-generated method stub    
    return new AbstractModule() {      
      public void configure()  {
        super.bind(JdbcDriver.class);
      }
    }; 
  }
  
  public static void main(String...s) throws ClassNotFoundException, SQLException {
    Injector injector = Guice.createInjector(new ConfigurationModule().export("MxAutoConfiguration"),new JdbcModule().export(""));
    System.out.println(injector.getInstance(JdbcDriver.class).saveOrUpdate("insert into test(id,name) values (?,?)","ccxca","22"));
    System.out.println(injector.getInstance(JdbcDriver.class).saveOrUpdate("update test set name=? where id =?","21ac","43"));
  }
}
