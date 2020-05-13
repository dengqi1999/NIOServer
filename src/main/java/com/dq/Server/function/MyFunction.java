package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;

import java.nio.channels.SelectionKey;

public interface MyFunction {
    public void run(SelectionKey key, JSONObject object);
}
