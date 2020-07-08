package com.mcvalls.concurrencyutils.processor.test;

public interface ConcurrencyTest {
	
	default void waitSeconds(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

}
