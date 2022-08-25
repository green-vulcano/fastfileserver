package com.greenvulcano.fastfileserver;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FileServerTest {
	
	@Test
	public void mainTest() throws Exception {
		boolean test = true;
		try {
			// String[] args = {};
			// FileServer.main(args);
		} catch (Exception e) {
			test = false;
			System.out.println(e.getMessage());
		}

		assertTrue("FileServer Test main: " + test, test);
	}
}