package com.dq.Server.controller;

import com.alibaba.fastjson.JSONObject;
import com.dq.Msg;
import com.dq.Server.Entity.ListDto;
import com.dq.Server.Entity.User;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 维护的系统在线人员
 */
public class UserController {
    public static ConcurrentHashMap<Integer, SelectionKey> usersChannel=new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, User> users=new ConcurrentHashMap<>();

    public static ConcurrentHashMap<Integer,User> systemUser=new ConcurrentHashMap<>();
    static {//创建默认用户
        User admin=new User();
        admin.setId(1);
        admin.setName("my first");
        admin.setPassword("admin");
        systemUser.put(admin.getId(),admin);
        User admin2=new User();
        admin2.setId(2);
        admin2.setName("my second");
        admin2.setPassword("admin");
        systemUser.put(admin2.getId(),admin2);
        User admin3=new User();
        admin3.setId(3);
        admin3.setName("my third");
        admin3.setPassword("admin");
        systemUser.put(admin3.getId(),admin3);
        User admin4=new User();
        admin4.setId(4);
        admin4.setName("my forth");
        admin4.setPassword("admin");
        systemUser.put(admin4.getId(),admin4);
        //加载群信息
        GroupController.getGroup(1);
    }

    public static boolean login(User user,SelectionKey selectionKey){
        if(systemUser.get(user.getId())==null)return false;
        if(systemUser.get(user.getId()).getPassword().equals(user.getPassword())){
            add(systemUser.get(user.getId()),selectionKey);
            return true;
        }else{
            return false;
        }
    }
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

    public static void sendToAllUser(Msg msg,Integer userId) throws IOException {
        Set<Map.Entry<Integer,SelectionKey>> set=  usersChannel.entrySet();
        for(Map.Entry entry:set){
            //指定某一人不发送
            if(entry.getKey().equals(userId))continue;
            SocketChannel channel= (SocketChannel) ((SelectionKey)entry.getValue()).channel();
            channel.write(ByteBuffer.wrap(JSONObject.toJSONString(msg).getBytes()));
        }
    }
    public static void sendToUser(Integer userId,Msg msg) throws IOException {
            SocketChannel channel= (SocketChannel) usersChannel.get(userId).channel();
            channel.write(ByteBuffer.wrap(JSONObject.toJSONString(msg).getBytes()));
    }
    public static List<ListDto> getUserList(){
        List<ListDto>result=new ArrayList<>(users.size());
        Enumeration<User> list=users.elements();
        while(list.hasMoreElements()){
            ListDto dto=new ListDto();
            User user=list.nextElement();
            dto.setId(user.getId());
            dto.setIsGroup(0);
            dto.setName(user.getName());
            result.add(dto);
        }
        return result;
    }
    public static void deleteUser(Integer userId){
        users.remove(userId);
        usersChannel.remove(userId);
    }
}
