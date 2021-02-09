package com.github.javademo.filehandler.configuration;

import static com.github.javademo.filehandler.Application.exit;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class ExitTimer {

  @Value("${timeout}")
  private long timeout;

  @Autowired private TaskScheduler taskScheduler;

  private ScheduledFuture<?> future;

  @PostConstruct
  public void init() {
    restart();
  }

  public void restart() {
    if (future != null) {
      future.cancel(false);
    }
    future = taskScheduler.schedule(() -> exit(), newExitDate());
  }

  private Date newExitDate() {
    return new Date(System.currentTimeMillis() + timeout * 1000);
  }
}
