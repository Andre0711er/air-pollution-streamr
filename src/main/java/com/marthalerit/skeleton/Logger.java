package com.marthalerit.skeleton;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Logger {
  private static final String LOGBACK_CONFIGFILE = "logback.xml";

  static {
    final String PROPERTY_NAME = "logback.configurationFile";
    // skip, if configuration file is defined. so logback will loaded itself
    if (System.getProperty(PROPERTY_NAME) == null) {
      List<String> searchPaths = new ArrayList<>();
      if (System.getProperty("config.folder") != null) {
        searchPaths.add(System.getProperty("config.folder") + File.separator + LOGBACK_CONFIGFILE);
      }
      searchPaths.add(System.getProperty("user.home") + "/conf/local/" + LOGBACK_CONFIGFILE);
      searchPaths.add(System.getProperty("user.home") + "/conf/" + LOGBACK_CONFIGFILE);
      searchPaths.add(new File(".").getAbsolutePath() + "/conf/local/" + LOGBACK_CONFIGFILE);
      searchPaths.add(new File(".").getAbsolutePath() + "/conf/" + LOGBACK_CONFIGFILE);
      searchPaths.add("conf/local/" + LOGBACK_CONFIGFILE);
      searchPaths.add("conf/" + LOGBACK_CONFIGFILE);
      searchPaths.add(new File("").getAbsolutePath() + "/src/main/resources/conf/local/" + LOGBACK_CONFIGFILE);
      searchPaths.add(new File("").getAbsolutePath() + "/src/main/resources/conf/" + LOGBACK_CONFIGFILE);
      for (String searchPath : searchPaths) {
        final File f = new File(searchPath);
        if (f.exists() && f.canRead()) {
          System.setProperty(PROPERTY_NAME, f.getAbsolutePath());
          System.out.println("use logging file; Path: " + f.getAbsolutePath());
          break;
        }
      }
    }
  }

  /**
   * get logger for given name
   *
   * @param name logger name
   * @return logger
   */
  public static org.slf4j.Logger getLogger(String name) {
    return LoggerFactory.getLogger(name);
  }

  /**
   * get logger for given class
   *
   * @param clazz class object
   * @return logger for class
   */
  public static org.slf4j.Logger getLogger(@SuppressWarnings("rawtypes") Class clazz) {
    return LoggerFactory.getLogger(clazz);
  }
}
