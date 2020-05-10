package com.dq.Server.controller;

import com.dq.Server.Entity.User;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维护的系统在线人员
 */
public class UserController {
    private static ConcurrentHashMap<Integer, SelectionKey> usersChannel=new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Integer, User> users=new ConcurrentHashMap<>();
    public static void add(User user,SelectionKey selectionKey){
        users.put(user.getId(),user);
        usersChannel.put(user.getId(),selectionKey);
    }
    public static SelectionKey getSelectionKey(Integer userID){
        return usersChannel.get(userID);
    }
    public static User getUser(Integer userId){
        return users.get(userId);
    }
    public static void deleteUser(Integer userId){
        users.remove(userId);
        usersChannel.remove(userId);
    }
}
