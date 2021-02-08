package com.github.javademo.filehandler.file;

import static java.lang.String.format;
import static java.nio.file.Files.probeContentType;
import static java.util.Arrays.asList;
import static java.util.EnumSet.of;
import static java.util.stream.Stream.of;
import static org.apache.commons.io.FilenameUtils.removeExtension;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.integration.file.FileNameGenerator;
import org.springframework.messaging.Message;

public enum FileType {
  NOT_SUPPORTED,
  NOT_DEFINED,
  IMAGE(
      "image",
      asList("image/bmp", "image/jpeg", "image/jpg", "image/gif", "image/png"),
      filename("-hist.bmp")),
  TEXT("text", asList("text/plain"), filename("-wp.txt"));

  private String typename;

  private List<String> contentTypes;

  private FileNameGenerator fileNameGenerator;

  private FileType() {}

  private FileType(
      String typename, List<String> contentTypes, FileNameGenerator fileNameGenerator) {
    this.typename = typename;
    this.contentTypes = contentTypes;
    this.fileNameGenerator = fileNameGenerator;
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
    return of(values()).filter(e -> e.isValid());
  }

  public static FileType getType(File file) {
    try {
      return getType(probeContentType(file.toPath()));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static String getErrorDirectory(File file) {
    return getType(file).getErrorDirectory();
  }

  private static FileType getType(String contentType) {
    if (contentType == null) {
      return NOT_DEFINED;
    }
    return Stream.of(values())
        .filter(e -> e.contentTypes != null && e.contentTypes.contains(contentType))
        .findFirst()
        .orElse(NOT_SUPPORTED);
  }

  private static String getFilenameWithoutExtension(Message<?> message) {
    return removeExtension((String) message.getHeaders().get("file_name"));
  }

  private static FileNameGenerator filename(String postfix) {
    return message -> format("%s%s", getFilenameWithoutExtension(message), postfix);
  }
}
