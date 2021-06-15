package com.marthalerit.cron;

import com.marthalerit.skeleton.Skeleton;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
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

    // run every day at 00:00 h
//    job = JobBuilder.newJob(Indexes.class).withIdentity("JobName", "JobGroup").build();
//    cronTrigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?")).build();
//    scheduler.scheduleJob(job, cronTrigger);
  }
}
