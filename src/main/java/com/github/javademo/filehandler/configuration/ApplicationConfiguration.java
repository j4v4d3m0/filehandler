package com.github.javademo.filehandler.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.javademo.filehandler.transform.ImageHistogramCreator;
import com.github.javademo.filehandler.transform.WordpairCounterFileCreator;

@Configuration
public class ApplicationConfiguration {

  @Bean
  public ImageHistogramCreator imageHistogramCreator() {
    return new ImageHistogramCreator();
  }

  @Bean
  public WordpairCounterFileCreator wordpairCounterFileCreator(
      @Value("${wordpair.number.limit}") int wordpairNumberLimit) {
    return new WordpairCounterFileCreator().withWordpairNumberLimit(wordpairNumberLimit);
  }
}
