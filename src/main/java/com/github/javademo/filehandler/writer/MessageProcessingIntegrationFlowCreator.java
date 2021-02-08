package com.github.javademo.filehandler.writer;

import static com.github.javademo.filehandler.file.FileType.IMAGE;
import static com.github.javademo.filehandler.file.FileType.TEXT;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.StandardIntegrationFlow;
import org.springframework.integration.file.FileWritingMessageHandler;
import org.springframework.integration.handler.LoggingHandler;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;
import com.github.javademo.filehandler.file.FileType;
import com.github.javademo.filehandler.transform.Creator;

@Component
public class MessageProcessingIntegrationFlowCreator {

  @Autowired private File inboundReadDirectory;

  @Autowired private Creator imageHistogramCreator;

  @Autowired private Creator wordpairCounterFileCreator;

  @Bean
  public IntegrationFlow imageFileCreatorFlow() {
    return createFlow(IMAGE, imageHistogramCreator);
  }

  @Bean
  public IntegrationFlow textFileCreatorFlow() {
    return createFlow(TEXT, wordpairCounterFileCreator);
  }

  private StandardIntegrationFlow createFlow(FileType e, Creator imageFileCreator) {
    return IntegrationFlows.from(e.getInboundChannel())
        .transform(imageFileCreator)
        .handle(fileWritingMessageHandler(e))
        .log(LoggingHandler.Level.INFO)
        .get();
  }

  private MessageHandler fileWritingMessageHandler(FileType e) {
    FileWritingMessageHandler handler =
        new FileWritingMessageHandler(new File(inboundReadDirectory, e.getOutDirectory()));
    handler.setAutoCreateDirectory(true);
    handler.setFileNameGenerator(e.getFileNameGenerator());
    return handler;
  }
}
