package com.alame.lab7.server.network;

import com.alame.lab7.common.network.Frame;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.server.App;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Arrays;
import java.util.logging.Logger;

public class ResponseSender {
	private final DatagramChannel datagramChannel;
	private final Logger logger = App.logger;

	public ResponseSender(DatagramChannel datagramChannel) {
		this.datagramChannel = datagramChannel;
	}

	public void sendResponse(SocketAddress socketAddress, Response<?> response) throws IOException {
		byte[] bytes = SerializationUtils.serialize(response);
		int step = 4000;
		int current = 0;
		int next = Math.min(current + step, bytes.length);
		while(next!= bytes.length){
			Frame frame = new Frame(Arrays.copyOfRange(bytes, current, next), false);
			datagramChannel.send(ByteBuffer.wrap(SerializationUtils.serialize(frame)),socketAddress);
			current = next;
			next = Math.min(current + step, bytes.length);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		Frame frame = new Frame(Arrays.copyOfRange(bytes, current, next+1), true);

		datagramChannel.send(ByteBuffer.wrap(SerializationUtils.serialize(frame)),socketAddress);
		logger.info("ответ " + response + " отправлен");
	}
}
