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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.ImmutableConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.YAMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;

import com.github.x19990416.macrossx.guice.modules.configuration.IMxConfiguration;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class MxAutoConfiguration implements IMxConfiguration {
	HashMap<String, ImmutableConfiguration> configs = Maps.newHashMap();
	
	@Inject
	private Properties properties;
	
	public void setProperties(Properties properties) {
		
	}

	public Properties getProperties() {
		return this.properties;
	}
	
	private boolean isBootJar() {
		return this.getClass().getResource("").getPath().startsWith("file:");
	}

	private ImmutableConfiguration load(FileBasedConfiguration configuration, String filename)
			throws FileNotFoundException, ConfigurationException, IOException {
		if (configuration == null)
			return null;
		if (isBootJar()) {
			InputStream inputStream = this.getClass().getResourceAsStream(File.separator+filename);
			if(inputStream == null) return null;
			
			configuration.read(new InputStreamReader(inputStream));
		} else {
			URL fileURL = ClassLoader.getSystemResource(filename);
			if(fileURL == null) {
				return null;
			}
			
			configuration.read(new FileReader(new File(fileURL.getFile())));
		}
		return configuration;
	}

	public void load(String filename)  {
		try {
		String file_ext = filename.substring(filename.lastIndexOf(".") + 1, filename.length());
		ImmutableConfiguration im = null;
		switch (file_ext.toLowerCase()) {
		case "yml": {
			im = load(new YAMLConfiguration(), filename);
			break;
		}
		case "properties": {
			im = load(new PropertiesConfiguration(), filename);
			break;
		}
		case "xml": {
			im = load(new XMLConfiguration(), filename);
			break;

		}
		}
		if (im == null) {

		} else {
			Iterator <String> its = im.getKeys();
			while(its.hasNext()) {
				String key = its.next();
				properties.put(key, im.getString(key));
			}
			configs.put(filename, im);
		}}catch ( ConfigurationException| IOException e) {
			
		}
	}

	public String getString(String key) {
	
		for (ImmutableConfiguration im : configs.values()) {
			if (im.containsKey(key)) {
				return im.getString(key);
			}
		}
		return null;
	}
}
