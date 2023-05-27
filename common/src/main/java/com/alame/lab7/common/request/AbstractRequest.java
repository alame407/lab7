package com.alame.lab7.common.request;

import com.alame.lab7.common.user.User;

/**
 * abstract request that realize setter and getter for server
 */
public abstract class AbstractRequest implements Request{
    protected final User user;
    private ClientServerInterface server;
    public AbstractRequest(User user){
        this.user = user;
    }
    /**
     * set server for request
     * @param server - server to set
     */
    @Override
    public void setServer(ClientServerInterface server) {
        this.server = server;
    }

    /**
     * get server of request
     * @return server of request
     */
    public ClientServerInterface getServer() {
        return server;
    }
}
