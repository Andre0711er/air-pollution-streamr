package com.marthalerit.daemon;

import com.marthalerit.skeleton.Skeleton;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDaemon {
  private final static Logger logger = Skeleton.getLogger(AbstractDaemon.class);

  protected final List<Thread> threads = new ArrayList<>();

  /**
   * add a single task
   *
   * @param task class which implements Runnable
   */
  protected void addTask(Runnable task) {
    addTask(task, 1);
  }

  protected void addTask(Runnable task, final int count) {
    for (int i = 0; i < count; i++) {
      logger.debug("add thread to run. Task: {}; Count: {}", task.getClass().getName(), (i + 1));
      Thread thread = new Thread(task);
      thread.start();
      if (thread.isAlive()) {
        threads.add(thread);
      }
    }
  }

  protected Thread shutdown() {
    logger.info("stopping ...");
    for (Thread thread : threads) {
      if (thread.isAlive()) {
        thread.interrupt();
        threads.remove(thread);
      }
    }
    return new Thread();
  }

  protected static int getProcessorNumber() {
    // get the runtime object associated with the current Java application
    final Runtime runtime = Runtime.getRuntime();

    // get the number of processors available to the Java virtual machine
    return runtime.availableProcessors();
  }
}
