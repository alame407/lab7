package com.alame.lab7.server.threads;

import com.alame.lab7.common.request.Request;
import com.alame.lab7.server.network.RequestHandler;

import java.net.SocketAddress;

public class HandleThread extends Thread{
	private final Request request;
	private final SocketAddress socketAddress;
	private final RequestHandler requestHandler;

	public HandleThread(Request request, SocketAddress socketAddress, RequestHandler requestHandler) {
		this.request = request;
		this.socketAddress = socketAddress;
		this.requestHandler = requestHandler;
	}

	@Override
	public void run() {
		requestHandler.handle(request, socketAddress);
	}
}
