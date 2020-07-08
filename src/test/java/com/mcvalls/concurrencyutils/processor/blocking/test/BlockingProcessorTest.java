package com.mcvalls.concurrencyutils.processor.blocking.test;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

import com.mcvalls.concurrencyutils.processor.blocking.BlockingProcessor;
import com.mcvalls.concurrencyutils.processor.blocking.BlockingProcessorFuture;

public class BlockingProcessorTest {

	private BlockingProcessor<Long, List<Integer>> blockingProcessor;
	private static final int MAX = 10000;

	@Before
	public void setup() {
		this.blockingProcessor = new BlockingProcessor<>();
	}

	@Test
	public void testSingleThread() {
		this.blockingProcessor.setConcurrentThreads(1);
		test();
	}

	@Test
	public void testTwoThreads() {
		this.blockingProcessor.setConcurrentThreads(2);
		test();
	}

	@Test
	public void testFourThreads() {
		this.blockingProcessor.setConcurrentThreads(4);
		test();
	}

	@Test
	public void testEightThreads() {
		this.blockingProcessor.setConcurrentThreads(8);
		test();
	}

	private void test() {
		long start = System.currentTimeMillis();
		List<Integer> integers = IntStream.rangeClosed(1, MAX)
				.parallel()
				.mapToObj(this::callPush)
				.map(this::getFromFuture)
				.flatMap(List::stream)
				.collect(Collectors.toList());

		assertEquals(MAX * 3, integers.size());
		assertEquals(MAX / 10, integers.stream().filter(i -> i == 1).count());

		long end = System.currentTimeMillis();
		System.out.println("Test with " + this.blockingProcessor.getNumberOfThreads()
				+ " threads finished in " + (end - start) + " milliseconds");
	}

	private BlockingProcessorFuture<List<Integer>> callPush(Integer integer) {
		int id = integer % 10;
		return this.blockingProcessor.push(Long.valueOf(id), () -> List.of(id, id * 2, id * 4));
	}

	private List<Integer> getFromFuture(BlockingProcessorFuture<List<Integer>> future) {
		return future.waitUntilProcessed().orElseGet(ArrayList::new);
	}
}
