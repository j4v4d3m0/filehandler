package com.github.javademo.filehandler.transform;

import static com.github.javademo.filehandler.TestUtils.locateClasspathResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.File;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WordPairCounterFileCreatorTest {
  @Test
  void testCreate() {
    assertEquals(
        "test proba : 6\nproba test : 6\n",
        new WordpairCounterFileCreator()
            .withWordpairNumberLimit(4)
            .create(locateClasspathResource("/data/test_txt.txt"))
            .toString());
  }

  @Test
  void testCreateWhenFileNotFound() {
    Assertions.assertThrows(
        RuntimeException.class,
        () ->
            new WordpairCounterFileCreator()
                .withWordpairNumberLimit(4)
                .create(new File("notexists")));
  }
}