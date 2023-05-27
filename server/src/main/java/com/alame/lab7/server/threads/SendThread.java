package com.alame.lab7.server.threads;

import com.alame.lab7.common.response.Response;
import com.alame.lab7.server.App;
import com.alame.lab7.server.network.ResponseSender;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.logging.Logger;

public class SendThread extends Thread{
	private final SocketAddress socketAddress;
	private final Response<?> response;
	private final ResponseSender responseSender;
	private final Logger logger = App.logger;

	public SendThread(SocketAddress socketAddress, Response<?> response, ResponseSender responseSender) {
		this.socketAddress = socketAddress;
		this.response = response;
		this.responseSender = responseSender;
	}

	@Override
	public void run() {
		try {
			responseSender.sendResponse(socketAddress, response);
		} catch (IOException e) {
			logger.warning(e.getMessage());
		}
	}
}
