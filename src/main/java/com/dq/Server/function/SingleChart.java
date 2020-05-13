package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.FunctionType;
import com.dq.Msg;
import com.dq.Server.controller.UserController;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class SingleChart extends Function{

    public SingleChart(){
    }

    @Override
    public void run() {
        SelectionKey key=getKey();
        JSONObject object=getObject();

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
