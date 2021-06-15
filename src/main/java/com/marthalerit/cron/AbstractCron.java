package com.marthalerit.cron;

import com.marthalerit.skeleton.Skeleton;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;

import java.util.Properties;

public abstract class AbstractCron extends Skeleton {
  private final static Logger logger = Skeleton.getLogger(AbstractCron.class);

  protected static SchedulerFactory schedulerFactory = null;

  protected static Scheduler scheduler = null;

  static {
    logger.info("init ...");
    Properties quartzProperties = new Properties();
    quartzProperties.setProperty("org.quartz.scheduler.instanceName", "Air-Pollution-Streamr");
    quartzProperties.setProperty("org.quartz.scheduler.instanceId", "Air-Pollution-Streamr");
    // max 4 threads parallel
    quartzProperties.setProperty("org.quartz.threadPool.threadCount", "4");
    quartzProperties.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");

    try {
      schedulerFactory = new StdSchedulerFactory(quartzProperties);
      scheduler = schedulerFactory.getScheduler();
      scheduler.start();
    } catch (SchedulerException e) {
      logger.error(e.getMessage(), e);
    }
  }
}
