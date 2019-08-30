package com.github.x19990416.macrossx.guice.modules.configuration.autoconfigure;

import java.util.Properties;

import com.github.x19990416.macrossx.guice.modules.configuration.IMxConfiguration;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class MxAutoConfigurationModule extends AbstractModule {
	
	
	  @Override 
	  protected void configure() {
		  super.bind(IMxConfiguration.class).to(MxAutoConfiguration.class).in(Scopes.SINGLETON);
		  super.bind(Properties.class);
	  }
}
