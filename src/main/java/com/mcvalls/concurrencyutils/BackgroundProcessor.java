package com.mcvalls.concurrencyutils;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class BackgroundProcessor<KEY, TYPE> {

	private final ExecutorService executorService;
	private ConcurrentMap<KEY, Future<TYPE>> futuresMap = new ConcurrentHashMap<>();
	private Set<KEY> keySet = new ConcurrentSkipListSet<>();
	
	public BackgroundProcessor() {
		super();
		this.executorService = Executors.newSingleThreadExecutor();
	}
	
	public BackgroundProcessor(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public synchronized Future<TYPE> push(KEY key, Supplier<TYPE> supplierFunction) {
		if (keySet.contains(key)) {
			return futuresMap.get(key);
		}
		keySet.add(key);
		Future<TYPE> future = this.executorService.submit(this.getCallableTask(key, supplierFunction));
		this.futuresMap.put(key, future);
		return future;
	}
	
	private Callable<TYPE> getCallableTask(KEY key, Supplier<TYPE> supplierFunction) {
		return () -> {
			TYPE t = supplierFunction.get();
			keySet.remove(key);
			return t;
		};
	}
	
}
