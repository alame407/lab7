package com.alame.lab7.server.threads;

import com.alame.lab7.server.App;
import com.alame.lab7.server.network.RequestReceiver;

import java.io.IOException;
import java.util.logging.Logger;

public class ReceiveThread extends Thread{
	private final RequestReceiver requestReceiver;
	private final Logger logger = App.logger;

	public ReceiveThread(RequestReceiver requestReceiver) {
		this.requestReceiver = requestReceiver;
	}

	@Override
	public void run() {
		while (true) {
			try {
				requestReceiver.receive();
			} catch (IOException e) {
				logger.warning(e.getMessage());
			}
		}
	}
}
