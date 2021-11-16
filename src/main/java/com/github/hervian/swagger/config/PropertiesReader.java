package com.github.hervian.swagger.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Based on https://www.baeldung.com/java-accessing-maven-properties
 */
public class PropertiesReader {
  private Properties properties;

  public PropertiesReader(String propertyFileName) throws IOException {
    InputStream is = getClass().getClassLoader()
        .getResourceAsStream(propertyFileName);
    this.properties = new Properties();
    this.properties.load(is);
  }

  public String getProperty(String propertyName) {
    return this.properties.getProperty(propertyName);
  }

  public String getArtifactId(){
    return getProperty("artifactId");
  }

  public String getGroupId(){
    return getProperty("groupId");
  }

}
