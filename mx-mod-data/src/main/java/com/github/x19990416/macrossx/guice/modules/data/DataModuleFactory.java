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
package com.github.x19990416.macrossx.guice.modules.data;

import com.github.x19990416.macrossx.guice.modules.common.BaseModuleFactory;
import com.github.x19990416.macrossx.guice.modules.data.jdbc.JdbcModule;
import com.google.inject.AbstractModule;

public class DataModuleFactory extends BaseModuleFactory{

  public static String JDBC = "JDBC";
  
  @Override
  public AbstractModule export(String arg0) {
    if(!super.getModules().containsKey(arg0)) {
      switch(arg0) {
        case "JDBC" :{
          super.getModules().put(JDBC, new JdbcModule());
          break;
        }
        default: return null;
      }
    }
    return super.getModules().get(arg0);
  }

  @Override
  public String getName() {
    // TODO Auto-generated method stub
    return "DATA";
  }
  }

