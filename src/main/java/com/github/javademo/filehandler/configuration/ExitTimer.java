package com.github.javademo.filehandler.configuration;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class ExitTimer {

  @Value("${timeout}")
  private long timeout;

  @Autowired private TaskScheduler taskScheduler;

  @Autowired private ConfigurableApplicationContext ctx;

  private ScheduledFuture<?> future;

  @PostConstruct
  public void init() {
    restart();
  }

  public void restart() {
    if (future != null) {
      future.cancel(false);
    }
    future = taskScheduler.schedule(exit(), newExitDate());
  }

private Runnable exit(){return () -> ctx.close();}

  private Date newExitDate() {
    return new Date(System.currentTimeMillis() + timeout * 1000);
  }
}
