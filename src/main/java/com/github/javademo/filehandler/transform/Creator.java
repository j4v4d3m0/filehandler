package com.github.javademo.filehandler.transform;

import java.io.File;

public interface Creator {
  Object create(File file);
}
