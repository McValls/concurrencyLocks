package com.mcvalls.concurrencyutils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		BackgroundProcessor<Integer, Double> bp = new BackgroundProcessor<>();

		IntStream.range(1, 100)
			.mapToObj(i -> new Proceso(i, i % 4, bp))
			.forEach(p -> p.start());
		
		
		Thread.sleep(10000);
		new Proceso(100, 1, bp).start();
	}
	
	static class Proceso extends Thread {
		
		long id;
		Integer value;
		BackgroundProcessor<Integer, Double> bp;
		
		Proceso(long id, Integer value, BackgroundProcessor<Integer, Double> bp) {
			this.id = id;
			this.value = value;
			this.bp = bp;
		}
		
		@Override
		public void run() {
			Future<?> future = bp.push(value, () -> doSomething(this.value));
			try {
				System.out.println("Future get: " + future.get());
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println("Finished " + this.id);
		}
		
		Double doSomething(Integer integer) {
			try {
				Thread.sleep(2000);
				Double result = Math.pow(integer, 5.0);
				System.out.println(result);
				return result;
			} catch(InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
}
