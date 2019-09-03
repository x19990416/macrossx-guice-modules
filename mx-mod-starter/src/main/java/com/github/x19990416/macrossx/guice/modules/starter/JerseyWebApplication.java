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
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import com.github.x19990416.macrossx.guice.modules.common.Constants;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;
public class WebApplication extends Application {


  @Inject
  @Named(Constants.MX_SERVER_PORT)
  private Integer port;
  
  @Inject()
  @Named(Constants.MX_SERVER_NAME)
  @javax.annotation.Nullable
  private String server;
  
  @Override
  public void run() throws Exception{
    Server jettyServer = new Server(port);
    if(server !=null && !server.equals("/"))
      server = "/"+server;
    else server = "/";
    ServletContextHandler httpContext = new ServletContextHandler(jettyServer, server);
    httpContext.addEventListener(new GuiceServletContextListener () {
      @Override
      protected Injector getInjector() {
        return injector;
      }
    });
    httpContext.addFilter(GuiceFilter.class, "/*", null);
    jettyServer.setHandler(httpContext);
    jettyServer.start();
  }
}
