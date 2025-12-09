package com.yychat.model;

import java.util.*;

public class GroupChat {
    private String groupId;
    private String groupName;
    private String creator;
    private List<String> members;
    private Date createTime;
    
    public GroupChat(String groupId, String groupName, String creator) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.creator = creator;
        this.members = new ArrayList<>();
        this.members.add(creator); // 创建者自动加入群聊
        this.createTime = new Date();
    }
    
    public boolean addMember(String member) {
        if (!members.contains(member)) {
            members.add(member);
            return true;
        }
        return false;
    }
    
    public boolean removeMember(String member) {
        if (members.size() > 1 && members.contains(member)) { // 确保至少有一个成员
            return members.remove(member);
        }
        return false;
    }
    
    public boolean isMember(String member) {
        return members.contains(member);
    }
    
    // Getters and Setters
    public String getGroupId() {
        return groupId;
    }
    
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    
    public String getGroupName() {
        return groupName;
    }
    
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
    
    public String getCreator() {
        return creator;
    }
    
    public void setCreator(String creator) {
        this.creator = creator;
    }
    
    public List<String> getMembers() {
        return members;
    }
    
    public void setMembers(List<String> members) {
        this.members = members;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
}