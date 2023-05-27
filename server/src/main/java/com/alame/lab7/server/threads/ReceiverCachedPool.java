package com.alame.lab7.server.threads;

import java.util.concurrent.ExecutorService;

public class ReceiverCachedPool {
	private final ExecutorService executorService;

	public ReceiverCachedPool(ExecutorService executorService) {
		this.executorService = executorService;
	}
	public void submit(ReceiveThread task){
		executorService.submit(task);
	}
}
