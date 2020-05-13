package com.dq.Server.function;

import com.dq.Server.controller.UserController;

import java.nio.channels.SelectionKey;
import java.util.Map;
import java.util.Set;

public class CleanOnlineUser implements Runnable{
    @Override
    public void run() {
        Set<Map.Entry<Integer, SelectionKey>> set= UserController.usersChannel.entrySet();
        for(Map.Entry<Integer, SelectionKey> entry:set){
            if(entry.getValue().isValid()){
                continue;
            }else{
                UserController.deleteUser(entry.getKey());
            }
        }
    }
}
