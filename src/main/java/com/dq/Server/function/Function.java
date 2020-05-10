package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.FunctionType;

import java.nio.channels.SelectionKey;

public class Function implements Runnable{
    private FunctionType functionType;
    private SelectionKey key;
    private JSONObject object;
    public Function(FunctionType functionType){
        this.functionType=functionType;
    }

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

    public FunctionType getFunctionType() {
        return functionType;
    }

    @Override
    public void run() {

    }
}
