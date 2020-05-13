package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.Server.Entity.FunctionType;
import com.dq.Server.Entity.Msg;
import com.dq.Server.controller.GroupController;

import java.nio.channels.SelectionKey;

public class PeopleChart implements MyFunction{
    public void run(SelectionKey key,JSONObject object){
        Msg msg=new Msg();
        msg.setCode(FunctionType.PEOPLE_CHART.getCode());
        msg.setFromId(object.getInteger("toId"));
        msg.setMsg(object.getString("msg"));
        GroupController.sendToGroup(msg.getFromId(),object.getInteger("fromId"),msg);
    }
}
