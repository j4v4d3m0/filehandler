package com.github.javademo.filehandler.file;

import static com.github.javademo.filehandler.file.FileType.getValidTypes;
import static org.springframework.integration.dsl.IntegrationFlows.from;
import java.io.File;
import java.util.Arrays;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Pollers;
import org.springframework.integration.dsl.RouterSpec;
import org.springframework.integration.file.DefaultDirectoryScanner;
import org.springframework.integration.file.DirectoryScanner;
import org.springframework.integration.file.FileReadingMessageSource;
import org.springframework.integration.file.filters.AcceptOnceFileListFilter;
import org.springframework.integration.file.filters.CompositeFileListFilter;
import org.springframework.integration.router.MethodInvokingRouter;
import org.springframework.integration.transaction.DefaultTransactionSynchronizationFactory;
import org.springframework.integration.transaction.ExpressionEvaluatingTransactionSynchronizationProcessor;
import org.springframework.integration.transaction.PseudoTransactionManager;
import org.springframework.integration.transaction.TransactionSynchronizationFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
class FilePollingIntegrationFlow {

  private static final int POLLER_FIXED_DELAY = 1000;

  @Bean
  public IntegrationFlow inboundFileIntegration(
      TaskExecutor taskExecutor,
      TransactionSynchronizationFactory transactionSynchronizationFactory,
      MessageSource<File> fileReadingMessageSource,
      Consumer<RouterSpec<FileType, MethodInvokingRouter>> route) {
    return from(
            fileReadingMessageSource,
            c ->
                c.poller(
                    Pollers.fixedDelay(POLLER_FIXED_DELAY)
                        .taskExecutor(taskExecutor)
                        .transactionSynchronizationFactory(transactionSynchronizationFactory)
                        .transactional(transactionManager())))
        .<File, FileType>route(FileType::getType, route)
        .get();
  }

  @Bean
  public Consumer<RouterSpec<FileType, MethodInvokingRouter>> route() {
    return m -> getValidTypes().forEach(e -> m.channelMapping(e, e.getInboundChannel()));
  }

  @Bean
  public TaskExecutor taskExecutor(@Value("${thread.pool.size}") int poolSize) {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(poolSize);
    return taskExecutor;
  }

  @Bean
  public PseudoTransactionManager transactionManager() {
    return new PseudoTransactionManager();
  }

  @Bean
  public TransactionSynchronizationFactory transactionSynchronizationFactory(
      ApplicationContext applicationContext) {
    ExpressionParser parser = new SpelExpressionParser();
    ExpressionEvaluatingTransactionSynchronizationProcessor syncProcessor =
        new ExpressionEvaluatingTransactionSynchronizationProcessor();
    syncProcessor.setBeanFactory(applicationContext.getAutowireCapableBeanFactory());
    syncProcessor.setAfterRollbackExpression(
        parser.parseExpression(
            "payload.renameTo(new java.io.File(@inboundReadDirectory.path "
                + " + T(java.io.File).separator "
                + " + T(com.github.javademo.filehandler.file.FileType).getErrorDirectory(payload)"
                + " + T(java.io.File).separator "
                + " + payload.name))"));
    return new DefaultTransactionSynchronizationFactory(syncProcessor);
  }

  @Bean
  public FileReadingMessageSource fileReadingMessageSource(
      File inboundReadDirectory, DirectoryScanner directoryScanner) {
    FileReadingMessageSource source = new FileReadingMessageSource();
    source.setDirectory(inboundReadDirectory);
    source.setScanner(directoryScanner);
    source.setAutoCreateDirectory(true);
    return source;
  }

  @Bean
  public DirectoryScanner directoryScanner() {
    DirectoryScanner scanner = new DefaultDirectoryScanner();
    CompositeFileListFilter<File> filter =
        new CompositeFileListFilter<>(
            Arrays.asList(new AcceptOnceFileListFilter<>(), new CustomFileListFilter()));
    scanner.setFilter(filter);
    return scanner;
  }
}
