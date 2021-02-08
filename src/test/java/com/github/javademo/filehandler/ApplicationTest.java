package com.github.javademo.filehandler;

import static com.github.javademo.filehandler.Application.main;

import org.junit.jupiter.api.Test;

class ApplicationTest {

	@Test
	void testMain() {
		main(new String[] {});
	}

	@Test
	void testRun() {
		new Application().withIncomingDir("INCOMING").run();
	}

}
