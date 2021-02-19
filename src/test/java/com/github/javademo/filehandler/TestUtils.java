package com.github.javademo.filehandler;

import static org.awaitility.Awaitility.await;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

import com.github.javademo.filehandler.exception.FilehandlerRuntimeException;

public class TestUtils {

	public static final String FILE_FIXTURE_PATH = "/data/foo.txt";

	public static final String FILE_FIXTURE_NAME = "foo.txt";

	public static File locateClasspathResource(String resourceName) {
		try {
			return new ClassPathResource(resourceName).getFile();
		} catch (IOException e) {
			throw new FilehandlerRuntimeException(e);
		}
	}

	public static void assertThatDirectoryHasFiles(File directory, int expectedFileCount) throws Exception {
		await().until(() -> directory.listFiles().length == expectedFileCount);
	}

	public static void assertThatDirectoryIsEmpty(File directory) throws Exception {
		await().until(() -> directory.listFiles().length == 0);
	}

	public static boolean deleteRecursive(File path) throws FileNotFoundException {
		if (!path.exists())
			throw new FileNotFoundException(path.getAbsolutePath());
		boolean ret = true;
		if (path.isDirectory()) {
			for (File f : path.listFiles()) {
				ret = ret && deleteRecursive(f);
			}
		}
		return ret && path.delete();
	}
}
