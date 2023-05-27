package com.alame.lab7.server.threads;

import java.util.concurrent.ExecutorService;

public class HandlerCachedPool {
	private final ExecutorService handleService;

	public HandlerCachedPool(ExecutorService handleService) {
		this.handleService = handleService;
	}
	public void execute(HandleThread task){
		handleService.execute(task);
	}
}
