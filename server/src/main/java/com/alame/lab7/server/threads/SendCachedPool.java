package com.alame.lab7.server.threads;

import java.util.concurrent.ExecutorService;

public class SendCachedPool {
	private final ExecutorService executorService;

	public SendCachedPool(ExecutorService executorService) {
		this.executorService = executorService;
	}
	public void submit(SendThread sendThread){
		executorService.submit(sendThread);
	}
}
