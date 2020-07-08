package com.mcvalls.concurrencyutils.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Processor {

	protected int numberOfThreads;
	protected ExecutorService executorService;
	
	public Processor() {
		super();
		this.executorService = Executors.newSingleThreadExecutor();
	}
	
	public Processor(int numberOfThreads) {
		if (numberOfThreads < 1) {
			throw new IllegalArgumentException("Illegal number of threads, must be > 0");
		} else if (numberOfThreads == 1) {
			this.executorService = Executors.newSingleThreadExecutor();
		} else {
			this.executorService = Executors.newFixedThreadPool(numberOfThreads);
		}
		this.numberOfThreads = numberOfThreads;
	}
	
	public void setConcurrentThreads(int numberOfThreads) {
		if (numberOfThreads < 1) {
			throw new IllegalArgumentException("Illegal number of threads, must be > 0");
		} else if (numberOfThreads == 1) {
			this.executorService = Executors.newSingleThreadExecutor();
		} else {
			this.executorService = Executors.newFixedThreadPool(numberOfThreads);
		}
		this.numberOfThreads = numberOfThreads;
	}
	
	public int getNumberOfThreads() {
		return this.numberOfThreads;
	}
	
	
}
