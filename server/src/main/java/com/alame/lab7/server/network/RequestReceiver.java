package com.alame.lab7.server.network;

import com.alame.lab7.common.network.Frame;
import com.alame.lab7.common.network.NetworkUtils;
import com.alame.lab7.common.request.Request;
import com.alame.lab7.server.App;
import com.alame.lab7.server.threads.HandleThread;
import com.alame.lab7.server.threads.HandlerCachedPool;
import org.apache.commons.lang3.SerializationException;
import org.apache.commons.lang3.SerializationUtils;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.List;
import java.util.logging.Logger;

public class RequestReceiver {
	private final DatagramChannel datagramChannel;
	private final HandlerCachedPool handlerCachedPool;
	private final static int MAX_SERIALIZED_FRAME_SIZE = 6000;
	private final RequestHandler requestHandler;
	private final FrameMapper frameMapper;
	private final Logger logger = App.logger;
	public RequestReceiver(DatagramChannel datagramChannel, HandlerCachedPool handlerCachedPool,
	                       RequestHandler requestHandler, FrameMapper frameMapper){
		this.datagramChannel = datagramChannel;
		this.handlerCachedPool = handlerCachedPool;
		this.requestHandler = requestHandler;
		this.frameMapper = frameMapper;
	}
	public void receive() throws IOException {
		byte[] bufferResponse = new byte[MAX_SERIALIZED_FRAME_SIZE];
		SocketAddress clientAddress = datagramChannel.receive(ByteBuffer.wrap(bufferResponse));
		if(clientAddress!=null) {
			try {
				Frame frame = SerializationUtils.deserialize(bufferResponse);
				frameMapper.addFrameToUser(clientAddress, frame);
				if (frame.isLast()){
					List<Frame> frames = frameMapper.getFramesByUser(clientAddress);
					frameMapper.removeUser(clientAddress);
					try {
						Request request = SerializationUtils.deserialize(
								NetworkUtils.convertListFramesToByteArray(frames));
						logger.info("получен Request "+request);
						handlerCachedPool.execute(new HandleThread(request, clientAddress, requestHandler));
					}
					catch (SerializationException e){
						logger.info("пришел неизвестный request");
					}
				}
			}
			catch (SerializationException e){
				logger.info("пришел неизвестный пакет");
			}

		}
	}
}
