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
import java.util.EnumSet;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.server.ResourceConfig;
import com.github.x19990416.macrossx.guice.modules.common.Constants;
import com.google.common.collect.Maps;
import com.google.inject.servlet.GuiceFilter;
public class JerseyWebApplication extends Application {
  Map<String, ResourceConfig> resourceConfig = Maps.newHashMap();
  

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
    httpContext.addFilter(GuiceFilter.class, "/*", EnumSet.<javax.servlet.DispatcherType>of(javax.servlet.DispatcherType.REQUEST, javax.servlet.DispatcherType.ASYNC));
    httpContext.addServlet(DefaultServlet.class, "/*");
    jettyServer.setHandler(httpContext);
    jettyServer.start();
    jettyServer.join();
  }
}
