package com.github.javademo.filehandler;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@SpringBootApplication
@Command
public class Application implements Runnable {

  @Option(
      names = {"-i"},
      required = true)
  private String incomingDir;

  @Option(
      names = {"-n"},
      required = false,
      defaultValue = "3")
  private int wordpairNumberLimit;

  @Option(
      names = {"-t"},
      required = false,
      defaultValue = "1")
  private int threadPoolSize;

  @Option(
      names = {"-d"},
      required = false,
      defaultValue = "60")
  private int timeout;

  public static void main(String[] args) {
    new CommandLine(new Application()).execute(args);
  }

  @Override
  public void run() {
    Properties properties = new Properties();
    properties.put("input.path", incomingDir);
    properties.put("wordpair.number.limit", wordpairNumberLimit);
    properties.put("timeout", timeout);
    properties.put("thread.pool.size", threadPoolSize);
     new SpringApplicationBuilder().sources(Application.class).properties(properties).run();
  }

  public Application withIncomingDir(String incomingDir) {
    this.incomingDir = incomingDir;
    return this;
  }
}
