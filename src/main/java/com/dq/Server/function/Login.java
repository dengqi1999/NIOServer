package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.Server.Entity.FunctionType;
import com.dq.Server.Entity.Msg;
import com.dq.Server.Entity.User;
import com.dq.Server.Server;
import com.dq.Server.controller.UserController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

public class Login implements MyFunction{
    public Login() {
    }

    public void run(SelectionKey key,JSONObject object){

        User user=new User();
        user.setId(object.getInteger("fromId"));
        user.setPassword(object.getString("msg"));
        try {
            if(UserController.login(user,key)){
                Msg msg=new Msg();
                msg.setCode(FunctionType.LOGIN.getCode());
                msg.setMsg("success");
                //发送在线成员列表
                List data=UserController.getUserList();
                data.addAll(UserController.getUser(user.getId()).getGroups());
                msg.setData(data);
                //返回登录成功
                ((SocketChannel)key.channel()).write(ByteBuffer.wrap(JSONObject.toJSONString(msg).getBytes()));
                //设置消息发送人
                msg.setFromId(user.getId());
                msg.setData(UserController.getUser(user.getId()));
                //对所有在线成员发送登录消息
                UserController.sendToAllUser(msg,msg.getFromId());
            }else{
                Msg msg=new Msg();
                msg.setCode(FunctionType.LOGIN.getCode());
                msg.setMsg("fail");
                ((SocketChannel)key.channel()).write(ByteBuffer.wrap(JSONObject.toJSONString(msg).getBytes()));
            }
        } catch (IOException e) {//发生通道写错误。执行用户引用清空
            Server.pool.execute(new CleanOnlineUser());
            e.printStackTrace();
        }
    }

}
