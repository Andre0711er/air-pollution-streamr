package com.marthalerit.cron;

import com.marthalerit.luftdaten.Reader;
import com.marthalerit.skeleton.Skeleton;
import org.quartz.*;
import org.slf4j.Logger;

public class Cron extends AbstractCron implements Runnable {
  private final static Logger logger = Skeleton.getLogger(Cron.class);

  @Override
  public void run() {
    try {
      logger.info("Start Cron-Job Process.");

      jobs();
    } catch (Exception e) {
      logger.error(e.toString());
    }
  }

  /**
   * daily running jobs
   */
  private void jobs() throws SchedulerException {
    JobDetail job;
    CronTrigger cronTrigger;

    // run every 30 seconds
    job = JobBuilder.newJob(Reader.class).withIdentity("Reader", "Luftdaten").build();
    cronTrigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("15,45 * * * * ?")).build();
    scheduler.scheduleJob(job, cronTrigger);
  }
}
