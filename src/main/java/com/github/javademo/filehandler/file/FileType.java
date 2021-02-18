package com.github.javademo.filehandler.file;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.EnumSet.of;
import static java.util.stream.Stream.of;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.springframework.integration.file.FileHeaders;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.messaging.Message;
import com.github.javademo.filehandler.exception.FilehandlerRuntimeException;
import com.github.javademo.filehandler.transform.Creator;
import com.github.javademo.filehandler.transform.ImageHistogramCreator;
import com.github.javademo.filehandler.transform.WordpairCounterFileCreator;

public enum FileType {
  NOT_SUPPORTED,
  NOT_DEFINED,
  IMAGE(
      "image",
      asList("image/bmp", "image/jpeg", "image/jpg", "image/gif", "image/png"),
      ImageHistogramCreator.class,
      filename("-hist.bmp")),
  TEXT("text", asList("text/plain"), WordpairCounterFileCreator.class, filename("-wp.txt"));

  private String typename;

  private List<String> contentTypes;

  private FileNameGenerator fileNameGenerator;

  private Class<? extends Creator> creator;

  FileType() {}

  FileType(
      String typename,
      List<String> contentTypes,
      Class<? extends Creator> creator,
      FileNameGenerator fileNameGenerator) {
    this.typename = typename;
    this.contentTypes = contentTypes;
    this.creator = creator;
    this.fileNameGenerator = fileNameGenerator;
  }

  public Class<? extends Creator> getCreator() {
    return creator;
  }

  public FileNameGenerator getFileNameGenerator() {
    return fileNameGenerator;
  }

  public String getOutDirectory() {
    return format("%s-done", typename);
  }

  public String getErrorDirectory() {
    return format("%s-error", typename);
  }

  public String getInboundChannel() {
    return format("inbound-%s-channel", typename);
  }

  public boolean isValid() {
    return !of(NOT_DEFINED, NOT_SUPPORTED).contains(this);
  }

  public static Stream<FileType> getValidTypes() {
    return of(values()).filter(FileType::isValid);
  }

  public static FileType getType(File file) {
    try {
      return getType(Files.probeContentType(file.toPath()));
    } catch (IOException e) {
      throw new FilehandlerRuntimeException(e);
    }
  }

  public static String getErrorDirectory(File file) {
    return getType(file).getErrorDirectory();
  }

  private static FileType getType(String contentType) {
    if (contentType == null) {
      return NOT_DEFINED;
    }
    return of(values())
        .filter(e -> e.contentTypes != null && e.contentTypes.contains(contentType))
        .findFirst()
        .orElse(NOT_SUPPORTED);
  }

  private static String getFilenameWithoutExtension(Message<?> message) {
    return FilenameUtils.removeExtension((String) message.getHeaders().get(FileHeaders.FILENAME));
  }

  private static FileNameGenerator filename(String postfix) {
    return message -> format("%s%s", getFilenameWithoutExtension(message), postfix);
  }
}
