package com.dq.Server.Entity;

import javax.xml.stream.events.DTD;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    Integer id;
    String name;
    String password;
    List<Group>groups;
    public User() {
        this.groups=new ArrayList<>();
    }
    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.groups=new ArrayList<>();
    }

    public void joinGroup(Group group){
        groups.add(group);
    }
    public List<ListDto> getGroups(){
        List<ListDto>list=new ArrayList<>();
        for(Group group:groups){
            ListDto dto=new ListDto();
            dto.setIsGroup(1);
            dto.setId(group.getGroupId());
            dto.setName(group.getGroupName());
            list.add(dto);
        }
        return list;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
