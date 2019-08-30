package com.github.x19990416.macrossx.guice.modules.configuration;

import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class PropertiesModule extends AbstractModule {

	private Properties properties;

	public PropertiesModule(Properties properties) {
		this.properties = properties;
	}

	public void configure() {
		Names.bindProperties(binder(), properties);
	}

}
