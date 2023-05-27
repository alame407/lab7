package com.alame.lab7.common.network;

import java.io.Serializable;

public class Frame implements Serializable {
    private final byte[] data;
    private final boolean isLast;
    public Frame(byte[] data, boolean isLast){
        this.data = data;
        this.isLast = isLast;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isLast() {
        return isLast;
    }
}
