package com.github.javademo.filehandler.file;

import static com.github.javademo.filehandler.file.FileType.getValidTypes;
import static java.util.stream.Collectors.toList;
import java.io.File;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilePollingConfiguration {

  @Bean(name = "inboundReadDirectory")
  public File inboundReadDirectory(@Value("${input.path}") String path) {
    return makeDirectory(new File(path));
  }

  @Bean
  public List<File> outDirectories(File inboundReadDirectory) {
    return getValidTypes()
        .map(e -> makeDirectory(inboundReadDirectory, e.getOutDirectory()))
        .collect(toList());
  }

  @Bean
  public List<File> errorDirectories(File inboundReadDirectory) {
    return getValidTypes()
        .map(e -> makeDirectory(inboundReadDirectory, e.getErrorDirectory()))
        .collect(toList());
  }

  private File makeDirectory(File file) {
    file.mkdirs();
    return file;
  }

  private File makeDirectory(File path, String f) {
    return makeDirectory(new File(path, f));
  }
}
