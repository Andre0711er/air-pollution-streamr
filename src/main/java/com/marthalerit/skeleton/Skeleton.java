package com.marthalerit.skeleton;

public class Skeleton {
  public static int APPLICATION_ENV;

  public static final int DEVELOPMENT = 1;

  public static final int PRODUCTION = 2;

  static {
    final String env = Config.get("application.env");
    if (env != null && env.equalsIgnoreCase("development")) {
      APPLICATION_ENV = DEVELOPMENT;
    } else {
      APPLICATION_ENV = PRODUCTION;
    }
  }

  /**
   * get logger for given class
   *
   * @param clazz class object
   * @return logger for class
   */
  public static org.slf4j.Logger getLogger(@SuppressWarnings("rawtypes") final Class clazz) {
    return com.marthalerit.skeleton.Logger.getLogger(clazz);
  }
}
