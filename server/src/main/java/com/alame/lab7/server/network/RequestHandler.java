package com.alame.lab7.server.network;

import com.alame.lab7.common.request.ClientServerInterface;
import com.alame.lab7.common.request.Request;
import com.alame.lab7.common.response.Response;
import com.alame.lab7.server.threads.SendCachedPool;
import com.alame.lab7.server.threads.SendThread;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * class for receive request handle it and send response
 */
public class RequestHandler {
    private final ClientServerInterface server;
    private final SendCachedPool sendCachedPool;
    private final ResponseSender responseSender;
    public RequestHandler(ClientServerInterface server, SendCachedPool sendCachedPool, ResponseSender responseSender)
            throws IOException {
        this.server = server;
        this.sendCachedPool = sendCachedPool;
        this.responseSender = responseSender;
    }

    public void handle(Request request, SocketAddress socketAddress) {
        request.setServer(server);
        Response<?> response= request.handle();
        sendCachedPool.submit(new SendThread(socketAddress, response, responseSender));
    }
}
