package com.github.javademo.filehandler.transform;

import static com.github.javademo.filehandler.TestUtils.locateClasspathResource;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

class ImageHistogramCreatorTest {

  @Test
  void testCreate() {
    assertEquals(
        26678,
        ((byte[]) new ImageHistogramCreator().create(locateClasspathResource("/data/test_jpg.jpg")))
            .length);
  }
}
