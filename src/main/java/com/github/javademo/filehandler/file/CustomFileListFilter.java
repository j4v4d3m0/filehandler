package com.github.javademo.filehandler.file;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.integration.file.filters.FileListFilter;

public class CustomFileListFilter implements FileListFilter<File> {

  @Override
  public List<File> filterFiles(File[] files) {
    return of(files).filter(filter()).collect(toList());
  }

  private Predicate<? super File> filter() {
    return f -> !f.isDirectory() && FileType.getType(f).isValid();
  }
}
