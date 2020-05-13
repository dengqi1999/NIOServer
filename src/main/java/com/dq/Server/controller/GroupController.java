package com.dq.Server.controller;

import com.alibaba.fastjson.JSONObject;
import com.dq.FunctionType;
import com.dq.Msg;
import com.dq.Server.Entity.Group;
import com.dq.Server.Entity.User;
import com.dq.Server.function.Function;
import com.sun.java.swing.plaf.windows.WindowsTextAreaUI;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

public class GroupController {
    public static ConcurrentHashMap<Integer, Group> groups=new ConcurrentHashMap<>();

    static {
        Group group=new Group(1,"first group");
        group.addMember(UserController.systemUser.get(1));
        UserController.systemUser.get(1).joinGroup(group);
        group.addMember(UserController.systemUser.get(2));
        UserController.systemUser.get(2).joinGroup(group);
        groups.put(group.getGroupId(),group);

        Group group1=new Group(2,"second group");
        group1.addMember(UserController.systemUser.get(1));
        UserController.systemUser.get(1).joinGroup(group1);
        group1.addMember(UserController.systemUser.get(2));
        UserController.systemUser.get(2).joinGroup(group1);
        group1.addMember(UserController.systemUser.get(3));
        UserController.systemUser.get(3).joinGroup(group1);
        group1.addMember(UserController.systemUser.get(4));
        UserController.systemUser.get(4).joinGroup(group1);
        groups.put(group1.getGroupId(),group1);
    }

    public static void addGroup(Group group){
        groups.put(group.getGroupId(),group);
    }
    public static Group getGroup(Integer groupId){
            return groups.get(groupId);
    }
    public static void sendToGroup(Integer groupId, Integer userId, Msg msg) {
        Group group=groups.get(groupId);
        System.out.println(group.getGroupName());
        for(User user:group.getMember()){

            if(user.getId()==userId)continue;
            msg.setToId(user.getId());
            try {
                UserController.sendToUser(user.getId(),msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
