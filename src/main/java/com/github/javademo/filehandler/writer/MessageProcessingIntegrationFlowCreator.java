package com.github.javademo.filehandler.writer;

import static com.github.javademo.filehandler.file.FileType.getValidTypes;
import static org.springframework.integration.dsl.IntegrationFlows.from;

import java.io.File;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import com.github.javademo.filehandler.configuration.ExitTimer;
import com.github.javademo.filehandler.file.FileType;

@Component
public class MessageProcessingIntegrationFlowCreator {

  @Autowired private File inboundReadDirectory;

  @Autowired private IntegrationFlowContext integrationFlowContext;

  @Autowired private ApplicationContext applicationContext;

  @Autowired private ExitTimer exitTimer;

  @Bean
  public List<IntegrationFlow> createTransfromFlows() {
    return getValidTypes().map(map()).collect(Collectors.toList());
  }

  private Function<? super FileType, ? extends IntegrationFlow> map() {
    return t -> integrationFlowContext.registration(createFlow(t)).register().getIntegrationFlow();
  }

  private StandardIntegrationFlow createFlow(FileType e) {
    return from(e.getInboundChannel())
        .transform(transform(e))
        .handle(fileWritingMessageHandler(e))
        .log(LoggingHandler.Level.INFO)
        .get();
  }

  private GenericTransformer<File, Object> transform(FileType e) {
    return file -> {
      exitTimer.restart();
      return applicationContext.getBean(e.getCreator()).create(file);
    };
  }

  private MessageHandler fileWritingMessageHandler(FileType e) {
    FileWritingMessageHandler handler =
        new FileWritingMessageHandler(new File(inboundReadDirectory, e.getOutDirectory()));
    handler.setAutoCreateDirectory(true);
    handler.setFileNameGenerator(e.getFileNameGenerator());
    return handler;
  }
}
