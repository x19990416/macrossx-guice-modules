package com.github.x19990416.macrossx.guice.modules.configuration;

import com.github.x19990416.macrossx.guice.modules.common.MxBaseModule;
import com.github.x19990416.macrossx.guice.modules.configuration.autoconfigure.MxAutoConfiguration;
import com.github.x19990416.macrossx.guice.modules.configuration.autoconfigure.MxAutoConfigurationModule;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MxConfigurationModule extends MxBaseModule {
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ConfigurationModule";
	}

	@Override
	public AbstractModule export(String key) {
	    if(!super.getModules().containsKey(key)) {
	      switch(key){
	        case "MxAutoConfiguration":{
	          Injector injector = Guice.createInjector(new MxAutoConfigurationModule());
	          IMxConfiguration configuration = injector.getInstance(MxAutoConfiguration.class);
	          configuration.load("config.yml");
	          configuration.load("config.properties");
	          configuration.load("config.xml");
	          super.getModules().put(key, new PropertiesModule(configuration.getProperties()));
	          break;
	        }
	      }	      
	    }
	    return super.getModules().get(key);
	}
}
