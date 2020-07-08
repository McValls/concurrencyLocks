package com.mcvalls.concurrencyutils.processor.background;

import com.mcvalls.concurrencyutils.processor.Processor;

/**
 * Process a task independently and do not forces the consumer of this class to wait
 * upon completion.
 * 
 * By default, all the submitted tasks are queued in a single thread executor, but this is modifiable
 * by the setConcurrentThreads method.
 * 
 * 
 * @author claudiovalls
 * @since 2020-07-06
 *
 */
public class BackgroundProcessor extends Processor {

	public BackgroundProcessor() {
		super();
	}
	
	public BackgroundProcessor(int numberOfThreads) {
		super(numberOfThreads);
	}
	
	public void push(Runnable task) {
		super.executorService.submit(task);
	}
		
}
