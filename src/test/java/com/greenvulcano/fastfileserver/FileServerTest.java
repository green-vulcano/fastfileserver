package com.greenvulcano.fastfileserver;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FileServerTest {
	
	@Test
	public void testTest() throws Exception {
		boolean test = true;
		try {
			FileServer.test();
		} catch (Exception e) {
			test = false;
			System.out.println(e.getMessage());
		}

		assertTrue("FileServer Test test: " + test, test);
	}
}