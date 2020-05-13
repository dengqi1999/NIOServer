package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;

import java.nio.channels.SelectionKey;

public class Function implements Runnable{
    private SelectionKey key;
    private JSONObject object;

    public JSONObject getObject() {
        return object;
    }

    public void setObject(JSONObject object) {
        this.object = object;
    }

    public void setKey(SelectionKey key) {
        this.key = key;
    }

    public SelectionKey getKey() {
        return key;
    }

    @Override
    public void run() {

    }
}
