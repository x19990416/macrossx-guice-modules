/*
 * Copyright (C) 2019 The mx-mod-configration Authors
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
package com.github.x19990416.macrossx.guice.modules.configuration.autoconfigure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

public class MxAutoConfiguration implements IMxConfiguration {

  private boolean isBootJar() {
    return this.getClass().getResource("").getPath().startsWith("jar");
  }
  
  private static YAMLConfiguration config =new YAMLConfiguration();


  @Override
  public void load() {
   if (isBootJar()) {
     try {
      config.read( ClassLoader.getSystemResourceAsStream("config.yml"));
    } catch (ConfigurationException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    }else {
      try {
        config.read(this.getClass().getClassLoader().getResourceAsStream("config.yml"));
      } catch ( ConfigurationException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }      
    } 
   Iterator<String> it = config.getKeys();
       while(it.hasNext()) {
         String key= it.next();
         System.out.println(key+'\t'+config.getString(key));
       }

  }

  public String read(String key) {


    return config.getString(key);
  }


  public static void main(String... s) {
    MxAutoConfiguration mxc = new MxAutoConfiguration();
    mxc.load();

    System.out.println("done\t" + mxc.read("testa"));

  }
}
