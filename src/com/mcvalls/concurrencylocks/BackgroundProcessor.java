package com.mcvalls.concurrencylocks;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public class BackgroundProcessor<T, K> {

	private final ExecutorService executorService;
	private ConcurrentMap<K, Future<T>> futuresMap = new ConcurrentHashMap<>();
	private Set<K> keySet = new ConcurrentSkipListSet<>();
	
	public BackgroundProcessor() {
		super();
		this.executorService = Executors.newSingleThreadExecutor();
	}
	
	public BackgroundProcessor(ExecutorService executorService) {
		this.executorService = executorService;
	}
	
	public synchronized Future<T> push(K key, Supplier<T> supplierFunction) {
		if (keySet.contains(key)) {
			return futuresMap.get(key);
		}
		keySet.add(key);
		Future<T> future = this.executorService.submit(this.getCallableTask(key, supplierFunction));
		this.futuresMap.put(key, future);
		return future;
	}
	
	private Callable<T> getCallableTask(K key, Supplier<T> supplierFunction) {
		return () -> {
			T t = supplierFunction.get();
			keySet.remove(key);
			return t;
		};
	}
	
}
