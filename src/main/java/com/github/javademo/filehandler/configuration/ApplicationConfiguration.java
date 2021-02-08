package com.github.javademo.filehandler.configuration;

import static com.github.javademo.filehandler.file.FileType.getValidTypes;
import static java.util.stream.Collectors.toList;
import static org.springframework.integration.dsl.MessageChannels.direct;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.MessageChannel;
import com.github.javademo.filehandler.transform.Creator;
import com.github.javademo.filehandler.transform.ImageHistogramCreator;
import com.github.javademo.filehandler.transform.WordpairCounterFileCreator;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public List<MessageChannel> inboundChannels() {
    return getValidTypes().map(e -> direct().get()).collect(toList());
  }

  @Bean
  public Creator imageHistogramCreator() {
    return new ImageHistogramCreator();
  }

  @Bean
  public Creator wordpairCounterFileCreator(
      @Value("${wordpair.number.limit}") int wordpairNumberLimit) {
    return new WordpairCounterFileCreator().withWordpairNumberLimit(wordpairNumberLimit);
  }
}
