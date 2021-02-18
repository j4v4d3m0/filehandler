package com.github.javademo.filehandler.exception;

import java.io.IOException;

public class FilehandlerRuntimeException extends RuntimeException {
  public FilehandlerRuntimeException(IOException e) {
    super(e);
  }

  private static final long serialVersionUID = 8000647918805619372L;
}
