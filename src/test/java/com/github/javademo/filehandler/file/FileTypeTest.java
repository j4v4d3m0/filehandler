package com.github.javademo.filehandler.file;

import static com.github.javademo.filehandler.TestUtils.locateClasspathResource;
import static com.github.javademo.filehandler.file.FileType.NOT_DEFINED;
import static com.github.javademo.filehandler.file.FileType.getType;
import static com.github.javademo.filehandler.file.FileType.valueOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FileTypeTest {

  @ParameterizedTest
  @CsvSource({
    "test_tiff.tiff,NOT_SUPPORTED",
    "test_jpg.jpg,IMAGE",
    "test_gif.gif,IMAGE",
    "test_png.png,IMAGE",
    "test_txt.txt,TEXT"
  })
  void testGetType(String filename, String expectedEnum) {
    assertEquals(valueOf(expectedEnum), getType(locateClasspathResource("/data/" + filename)));
  }

  @Test
  void testGetTypeWhenFileNotFoundThenNotDefined() {
    assertEquals(NOT_DEFINED, getType(new File("notexists")));
  }
}
