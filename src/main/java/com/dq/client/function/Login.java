package com.dq.client.function;

import com.dq.FunctionType;
import com.dq.Server.Entity.User;
import com.dq.Server.controller.UserController;

import java.nio.channels.SelectionKey;

public class Login extends Function {
    public Login() {
        super(FunctionType.LOGIN);
    }

    @Override
    public void run(){
        SelectionKey key=getKey();
        User user=new User(getObject().getInteger("id"),getObject().getString("name"),getObject().getString("password"));
        UserController.add(user,key);
        key.attach("登录成功");
        key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
    }

}
