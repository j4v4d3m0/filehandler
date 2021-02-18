package com.github.javademo.filehandler.file;

import static com.github.javademo.filehandler.TestUtils.assertThatDirectoryHasFiles;
import static com.github.javademo.filehandler.TestUtils.assertThatDirectoryIsEmpty;
import static com.github.javademo.filehandler.TestUtils.deleteRecursive;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.springframework.util.FileCopyUtils.copy;
import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.test.annotation.DirtiesContext;
import com.github.javademo.filehandler.TestUtils;
import com.github.javademo.filehandler.transform.ImageHistogramCreator;
import com.github.javademo.filehandler.transform.WordpairCounterFileCreator;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilePollingTest {

  @Autowired
  @Qualifier("inboundReadDirectory")
  public File inboundReadDirectory;

  @Autowired
  @Qualifier("outDirectories")
  public List<File> outDirectories;

  @Autowired
  @Qualifier("errorDirectories")
  public List<File> errorDirectories;

  @Autowired
  @Qualifier("inbound-text-channel")
  public DirectChannel filePollingChannel;

  @MockBean private WordpairCounterFileCreator wordpairCounterFileCreator;

  @MockBean private ImageHistogramCreator imageHistogramCreator;

  @AfterEach
  public void tearDown() throws Exception {
    deleteRecursive(inboundReadDirectory);
  }

  @Test
  void pollFindsValidFile() throws Exception {
    when(wordpairCounterFileCreator.create(Mockito.any())).thenReturn("Hello");

    final CountDownLatch latch = new CountDownLatch(1);
    filePollingChannel.addInterceptor(
        new ChannelInterceptor() {
          @Override
          public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
            latch.countDown();
          }
        });
    copy(
        TestUtils.locateClasspathResource(TestUtils.FILE_FIXTURE_PATH),
        new File(inboundReadDirectory, TestUtils.FILE_FIXTURE_NAME));
    assertThat(latch.await(5, TimeUnit.SECONDS), is(true));
    errorDirectories.forEach(
        d -> {
          try {
            assertThatDirectoryIsEmpty(d);
          } catch (Exception e) {
            fail(e);
          }
        });
    assertThatDirectoryIsEmpty(outDirectories.get(0));
    assertThatDirectoryHasFiles(outDirectories.get(1), 1);
  }

  @Test
  void pollFindsInvalidFile() throws Exception {
    when(wordpairCounterFileCreator.create(Mockito.any())).thenThrow(new RuntimeException());

    copy(
        TestUtils.locateClasspathResource(TestUtils.FILE_FIXTURE_PATH),
        new File(inboundReadDirectory, TestUtils.FILE_FIXTURE_NAME));

    outDirectories.forEach(
        d -> {
          try {
            assertThatDirectoryIsEmpty(d);
          } catch (Exception e) {
            fail(e);
          }
        });
    assertThatDirectoryIsEmpty(errorDirectories.get(0));
    assertThatDirectoryHasFiles(errorDirectories.get(1), 1);
  }
}
