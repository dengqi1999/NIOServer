package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.Server.Entity.FunctionType;
import com.dq.Server.Entity.Msg;
import com.dq.Server.Server;
import com.dq.Server.controller.UserController;

import java.io.IOException;
import java.nio.channels.SelectionKey;

public class Quit implements MyFunction{

    public void run(SelectionKey key, JSONObject object){
        //System.out.println("用户退出");
        Msg msg=new Msg();
        msg.setCode(FunctionType.QUIT.getCode());
        msg.setMsg("quit");
        //设置消息发送人
        msg.setFromId(object.getInteger("fromId"));
        //对所有在线成员发送下线消息
        try {
            UserController.sendToAllUser(msg,msg.getFromId());
        } catch (IOException e) {
            Server.pool.execute(new CleanOnlineUser());
            e.printStackTrace();
        }finally {
            UserController.deleteUser(msg.getFromId());
        }
    }
}
