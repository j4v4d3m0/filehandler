package com.github.javademo.filehandler.transform;

import static com.github.javademo.filehandler.TestUtils.locateClasspathResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.io.File;
import org.junit.jupiter.api.Test;
import com.github.javademo.filehandler.exception.FilehandlerRuntimeException;

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
    WordpairCounterFileCreator creator =
        new WordpairCounterFileCreator().withWordpairNumberLimit(4);
    File file = new File("notexists");
    
    assertThrows(FilehandlerRuntimeException.class, () -> creator.create(file));
  }
}
