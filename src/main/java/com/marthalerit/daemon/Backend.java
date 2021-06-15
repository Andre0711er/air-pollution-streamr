package com.marthalerit.daemon;

import com.marthalerit.skeleton.Skeleton;
import org.slf4j.Logger;

public class Backend extends AbstractDaemon {
  private final static Logger logger = Skeleton.getLogger(Backend.class);

  public void start() {
    logger.info("Start Backend");

    Runtime.getRuntime().addShutdownHook(shutdown());

    logger.info("starting ...");
    addTask(new com.marthalerit.cron.Cron());
  }

  public static void main(String... args) throws Exception {
    Backend backend = new Backend();
    backend.start();
  }
}
