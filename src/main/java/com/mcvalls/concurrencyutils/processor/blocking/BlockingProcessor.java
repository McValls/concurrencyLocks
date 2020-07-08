package com.mcvalls.concurrencyutils.processor.blocking;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Future;

import com.mcvalls.concurrencyutils.processor.Processor;

/**
 * Process a task and forces the consumer of this class to wait until the task is complete.
 * If the same task is submitted more than once, it will only be executed once and all the submitters
 * will get the same result.
 * A task is identified by a key that must implement efficiently both hashCode and equals methods.
 * 
 * By default, all the submitted tasks are queued in a single thread executor, but this is modifiable
 * by the setConcurrentThreads method.
 * 
 * @author mcvalls
 * @since 2020-07-06
 *
 * @param <KEY> Generic reference of the Key
 * @param <TYPE> Generic reference of the Type returned on completion.
 */
public class BlockingProcessor<KEY, TYPE> extends Processor {

	private ConcurrentMap<KEY, BlockingProcessorFuture<TYPE>> futuresMap = new ConcurrentHashMap<>();
	private Set<KEY> keySet = new ConcurrentSkipListSet<>();
	
	public BlockingProcessor() {
		super();
	}
	
	/**
	 * Creates a BlockingProcessor with the properly executor service. 
	 * @param numberOfThreads must be greater than 0, and it is recommended to be less or equals than 8.
	 */
	public BlockingProcessor(int numberOfThreads) {
		super(numberOfThreads);
	}
	
	/**
	 * 
	 * Execute the callable parameter and forces to the caller of this method to wait upon completion.
	 * If several threads calls this method with the same key, all of them will be notified at the same moment
	 * when the callable task have had finished.
	 * 
	 * @param key to recognize if a task is being already executed
	 * @param callable function that we want to wait upon completion
	 * @return a classic java.util.concurrent.Future wrapped in a class for client simplicity.
	 */
	public synchronized BlockingProcessorFuture<TYPE> push(KEY key, Callable<TYPE> callable) {
		if (keySet.contains(key)) {
			return futuresMap.get(key);
		}
		keySet.add(key);
		Future<TYPE> future = super.executorService.submit(callable);
		BlockingProcessorFuture<TYPE> processorFuture = new BlockingProcessorFuture<>(future, () -> keySet.remove(key));
		this.futuresMap.put(key, processorFuture);
		return processorFuture;
	}
	
}
