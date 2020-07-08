package com.mcvalls.concurrencyutils.processor.blocking.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.mcvalls.concurrencyutils.processor.blocking.BlockingProcessor;
import com.mcvalls.concurrencyutils.processor.test.ConcurrencyTest;

public class OrderSystemTest implements ConcurrencyTest {

	@Test
	public void testBackgroundProcessor() {
		BlockingProcessor<Integer, OrderProcessResponse> blockingProcessor = new BlockingProcessor<>();
		
		Order order1 = new Order(1, "Linux");
		Order order2 = new Order(2, "Mac");
		Order order3 = new Order(3, "Windows");
		
		OrderProcessor orderProcessor = new OrderProcessor();
		
		List<Order> orders = List.of(order1, order2, order1, order3);
		
		List<OrderProcessResponse> responses = orders.parallelStream()
			.map(order -> blockingProcessor.push(order.getId(), () -> orderProcessor.process(order)))
			.map(orderProcessed -> orderProcessed.waitUntilProcessed().orElse(null))
			.collect(Collectors.toList());
		
		waitSeconds(5);
		
		responses.add(blockingProcessor.push(order1.getId(), () -> orderProcessor.process(order1))
				.waitUntilProcessed().orElse(null));
		
		assertEquals(3l, responses.stream().filter(order -> order.getOrderId() == 1).count());
		assertEquals(1l, responses.stream().filter(order -> order.getOrderId() == 2).count());
		assertEquals(1l, responses.stream().filter(order -> order.getOrderId() == 3).count());
		assertTrue(responses.stream().filter(order -> order.getOrderId() == 1).findAny()
				.map(order -> order.getText().contains(order1.getDescription())).orElse(false));
		assertTrue(responses.stream().allMatch(order -> order.getCode() == order.getOrderId() % 2));
	}

	private class Order {

		private int id;
		private String description;

		private Order(int id, String description) {
			this.id = id;
			this.description = description;
		}

		int getId() {
			return id;
		}

		String getDescription() {
			return description;
		}

	}

	private class OrderProcessResponse {

		private int code;
		private String text;
		private int orderId;

		private OrderProcessResponse(int code, String text, int orderId) {
			super();
			this.code = code;
			this.text = text;
			this.orderId = orderId;
		}

		int getCode() {
			return code;
		}

		String getText() {
			return text;
		}
		
		int getOrderId() {
			return orderId;
		}
	}
	
	private class OrderProcessor {
		
		OrderProcessResponse process(Order order) {
			System.out.println("Processing order " + order.getId() + " [" + order.getDescription() + "]");
			waitSeconds(2);
			int code = order.getId() % 2;
			return new OrderProcessResponse(code, order.getDescription() + " with code " + code, order.getId());
		}
		
	}

}
