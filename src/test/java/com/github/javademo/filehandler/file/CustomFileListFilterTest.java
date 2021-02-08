package com.github.javademo.filehandler.file;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import com.github.javademo.filehandler.TestUtils;

class CustomFileListFilterTest {

  @TempDir File tempDir;

  @Test
  void testFilterFiles() {
    File validFile = TestUtils.locateClasspathResource("/data/test_jpg.jpg");
    File notValidFile = TestUtils.locateClasspathResource("/data/test_tiff.tiff");
    assertEquals(
        asList(validFile),
        new CustomFileListFilter().filterFiles(new File[] {validFile, tempDir, notValidFile}));
  }
}
