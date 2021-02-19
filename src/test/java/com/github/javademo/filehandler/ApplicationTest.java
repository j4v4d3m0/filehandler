package com.github.javademo.filehandler;

import static com.github.javademo.filehandler.Application.main;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class ApplicationTest {

	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	@Test
	void testMain() {
		captureOut();
		main(new String[] {});
		assertTrue(getOut().startsWith("Missing required option:"));
	}

	@Test
	void testRun() {
		await().until(() -> {
			new Application().withIncomingDir("INCOMING").run();
			return true;
		});
	}

	private void captureOut() {
		System.setErr(new PrintStream(outContent));
	}

	private String getOut() {
		return outContent.toString().replaceAll("\r\n", "");

	}

}
