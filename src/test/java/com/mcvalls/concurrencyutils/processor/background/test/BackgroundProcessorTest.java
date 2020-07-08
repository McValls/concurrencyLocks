package com.mcvalls.concurrencyutils.processor.background.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import com.mcvalls.concurrencyutils.processor.background.BackgroundProcessor;
import com.mcvalls.concurrencyutils.processor.test.ConcurrencyTest;

public class BackgroundProcessorTest implements ConcurrencyTest {

	@Test
	public void testBackgroundProcessing() {
		BackgroundProcessor backgroundProcessor = new BackgroundProcessor();
		
		List<Boolean> list = new LinkedList<>();
		backgroundProcessor.push(() -> {
			waitSeconds(1);
			list.add(Boolean.TRUE);
		});
		assertTrue(list.isEmpty());
		
		waitSeconds(2);
		
		assertEquals(1, list.size());
		assertEquals(Boolean.TRUE, list.get(0));
	}
	
}
