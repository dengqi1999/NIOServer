package com.dq.Server.function;

import com.alibaba.fastjson.JSONObject;
import com.dq.FunctionType;
import com.dq.Msg;
import com.dq.Server.Entity.User;
import com.dq.Server.controller.UserController;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class Login extends Function{
    public Login() {
        super(FunctionType.LOGIN);
    }

    @Override
    public void run(){
        SelectionKey key=getKey();
        User user=new User();
        user.setId(getObject().getInteger("fromId"));
        UserController.add(user,key);
        //key.attach("登录成功");
        key.interestOps(key.interestOps() | SelectionKey.OP_READ);
        key.selector().wakeup();
        Msg msg=new Msg();
        msg.setCode(FunctionType.SINGLE_CHART.getCode());
        msg.setMsg("登录成功");
        try {
            System.out.println(JSONObject.toJSONString(msg));
            ((SocketChannel)key.channel()).write(ByteBuffer.wrap(JSONObject.toJSONString(msg).getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
