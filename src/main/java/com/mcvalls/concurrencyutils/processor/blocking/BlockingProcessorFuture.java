package com.mcvalls.concurrencyutils.processor.blocking;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public class BlockingProcessorFuture<T> {

	private Future<T> future;
	private Supplier<?> onFinishSupplier;

	public BlockingProcessorFuture(Future<T> future, Supplier<?> onFinishSupplier) {
		super();
		this.future = future;
		this.onFinishSupplier = onFinishSupplier;
	}

	public Optional<T> waitUntilProcessed() {
		try {
			T value = future.get(5, TimeUnit.MINUTES);
			return Optional.ofNullable(value);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			return Optional.empty();
		} finally {
			onFinishSupplier.get();
		}
	}

}
