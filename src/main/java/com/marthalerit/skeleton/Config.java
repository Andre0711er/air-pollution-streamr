package com.marthalerit.skeleton;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Config {
  private static final String CONFIG_FILE = "config.xml";

  private static final XMLConfiguration config = new XMLConfiguration();

  static {
    List<String> searchPaths = new ArrayList<>();
    if (System.getProperty("config.folder") != null) {
      searchPaths.add(System.getProperty("config.folder") + File.separator + CONFIG_FILE);
    }
    searchPaths.add(System.getProperty("user.home") + "/conf/" + CONFIG_FILE);
    searchPaths.add(new File(".").getAbsolutePath() + "/conf/" + CONFIG_FILE);
    searchPaths.add("conf/" + CONFIG_FILE);
    searchPaths.add(new File("").getAbsolutePath() + "/src/main/resources/conf/" + CONFIG_FILE);
    for (String searchPath : searchPaths) {
      final File f = new File(searchPath);
      if (f.exists() && f.canRead()) {
        try {
          System.out.println("use config file; Path: " + f.getAbsolutePath());
          config.load(f);
        } catch (ConfigurationException e) {
          throw new RuntimeException(e.getMessage());
        }

        break;
      }
    }

    searchPaths.clear();
    // search for local config
    if (System.getProperty("config.folder") != null) {
      searchPaths.add(System.getProperty("config.folder") + "/local/" + CONFIG_FILE);
    }
    searchPaths.add(System.getProperty("user.home") + "/conf/local/" + CONFIG_FILE);
    searchPaths.add(new File(".").getAbsolutePath() + "/conf/local/" + CONFIG_FILE);
    searchPaths.add("conf/local/" + CONFIG_FILE);
    searchPaths.add(new File("").getAbsolutePath() + "/src/main/resources/conf/local/" + CONFIG_FILE);
    for (String searchPath : searchPaths) {
      final File f = new File(searchPath);
      if (f.exists() && f.canRead()) {
        try {
          XMLConfiguration localConfig = new XMLConfiguration(f);
          Iterator<String> keys = localConfig.getKeys();
          while (keys.hasNext()) {
            String key = keys.next();
            config.setProperty(key, localConfig.getProperty(key));
          }
          break;
        } catch (ConfigurationException e) {
          throw new RuntimeException(e.getMessage());
        }
      }
    }
  }

  public static boolean has(final String key) {
    return config.containsKey(key);
  }

  public static String get(final String key) {
    if (has(key)) {
      return config.getString(key);
    }
    return null;
  }

  public static Boolean getAsBoolean(final String key) {
    if (has(key)) {
      return config.getBoolean(key);
    }
    return null;
  }

  public static Integer getAsInteger(final String key) {
    return getAsInteger(key, null);
  }

  public static Integer getAsInteger(final String key, final Integer defaultValue) {
    if (has(key)) {
      return config.getInteger(key, defaultValue);
    }
    return null;
  }

  public static Double getAsDouble(final String key) {
    return getAsDouble(key, null);
  }

  public static Double getAsDouble(final String key, final Double defaultValue) {
    if (has(key)) {
      return config.getDouble(key, defaultValue);
    }
    return null;
  }

  /**
   * get sub list from key
   *
   * @param key which is requested
   * @return list with all values
   * @see <a href="http://commons.apache.org/proper/commons-configuration/userguide/howto_xml.html#Hierarchical_properties">http://commons.apache.org/proper/commons-configuration/userguide/howto_xml.html#Hierarchical_properties</a>
   */
  @SuppressWarnings("unchecked")
  public static List<String> getAsList(final String key) {
    if (has(key)) {
      final Object props = config.getProperty(key);
      if (props instanceof List) {
        return (List<String>) props;
      }
      if (props instanceof String) {
        List<String> returnData = new ArrayList<>();
        returnData.add(props.toString());

        return returnData;
      }
    }
    return null;
  }
}
