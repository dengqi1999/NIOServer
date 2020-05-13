package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.Server.Entity.FunctionType;
import com.dq.Server.Entity.Msg;
import com.dq.Server.controller.UserController;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class SingleChart implements MyFunction{

    public SingleChart(){
    }

    public void run(SelectionKey key,JSONObject object) {
        Msg msg=new Msg();
        msg.setFromId(object.getInteger("fromId"));
        msg.setCode(FunctionType.SINGLE_CHART.getCode());
        msg.setMsg(object.getString("msg"));
        msg.setToId(object.getInteger("toId"));
        try {
            UserController.sendToUser(msg.getToId(),msg);
        } catch (IOException e) {
            UserController.deleteUser(msg.getToId());
            e.printStackTrace();
        }
    }
}
