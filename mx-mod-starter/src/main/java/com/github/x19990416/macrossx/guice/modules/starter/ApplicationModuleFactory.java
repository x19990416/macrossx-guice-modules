/*
 * Copyright (C) 2019 The mx-mod-web-starter Authors
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
package com.github.x19990416.macrossx.guice.modules.starter;

import com.github.x19990416.macrossx.guice.modules.common.BaseModuleFactory;
import com.google.inject.AbstractModule;

public class ApplicationModuleFactory extends BaseModuleFactory {

  public static final String WEB_JERSEY_APPLICATION = "WebJerseyApplication";
  
  @Override
  public AbstractModule export(String key) {
    if(!super.getModules().containsKey(key)) {
      switch(key){
        case WEB_JERSEY_APPLICATION:{
          super.getModules().put(WEB_JERSEY_APPLICATION, new JerseyWebApplicationModule());
          break;
        }
      }       
    }
    return super.getModules().get(key);
  }

  @Override
  public String getName() { 
    // TODO Auto-generated method stub
    return "ApplicationModuleFactory";
  }

}
