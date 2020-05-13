package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.FunctionType;
import com.dq.Msg;
import com.dq.Server.Server;
import com.dq.Server.controller.UserController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Quit extends Function{

    @Override
    public void run(){
        SelectionKey key=getKey();
        Msg msg=new Msg();
        msg.setCode(FunctionType.QUIT.getCode());
        msg.setMsg("quit");
        //设置消息发送人
        msg.setFromId(getObject().getInteger("fromId"));
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
