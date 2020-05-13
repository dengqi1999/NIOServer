package com.dq.Server.Entity;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private Integer groupId;
    private String groupName;
    private List<User>member;

    public Group(Integer groupId,String groupName){
        this.groupId=groupId;
        this.groupName=groupName;
        this.member=new ArrayList<>();
    }

    public void addMember(User user){
        member.add(user);
    }

    public List<User> getMember(){
        return member;
    }
    public Integer getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
