package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.FunctionType;
import com.dq.Msg;
import com.dq.Server.controller.GroupController;

import java.nio.channels.SelectionKey;

public class PeopleChart extends Function{
    @Override
    public void run(){
        SelectionKey key=getKey();
        JSONObject object=getObject();
        Msg msg=new Msg();
        msg.setCode(FunctionType.PEOPLE_CHART.getCode());
        msg.setFromId(object.getInteger("toId"));
        msg.setMsg(object.getString("msg"));
        GroupController.sendToGroup(msg.getFromId(),object.getInteger("fromId"),msg);
    }
}
