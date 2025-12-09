package com.yychat.control;

import com.yychat.model.GroupChat;

import java.util.*;

public class GroupChatManager {
    // 存储所有群聊，key为groupId
    private static Map<String, GroupChat> groupChats = new HashMap<>();
    // 存储用户加入的群聊，key为用户名，value为群聊ID列表
    private static Map<String, List<String>> userGroups = new HashMap<>();
    
    // 创建群聊
    public static GroupChat createGroup(String groupId, String groupName, String creator) {
        if (groupChats.containsKey(groupId)) {
            return null; // 群聊已存在
        }
        
        GroupChat group = new GroupChat(groupId, groupName, creator);
        groupChats.put(groupId, group);
        
        // 将用户加入该群聊
        addToUserGroup(creator, groupId);
        
        return group;
    }
    
    // 获取群聊
    public static GroupChat getGroup(String groupId) {
        return groupChats.get(groupId);
    }
    
    // 删除群聊
    public static boolean deleteGroup(String groupId, String creator) {
        GroupChat group = groupChats.get(groupId);
        if (group != null && group.getCreator().equals(creator)) {
            // 从用户群聊列表中移除
            for (String member : group.getMembers()) {
                removeFromUserGroup(member, groupId);
            }
            groupChats.remove(groupId);
            return true;
        }
        return false;
    }
    
    // 添加成员到群聊
    public static boolean addMemberToGroup(String groupId, String member) {
        GroupChat group = groupChats.get(groupId);
        if (group != null && group.addMember(member)) {
            addToUserGroup(member, groupId);
            return true;
        }
        return false;
    }
    
    // 从群聊中移除成员
    public static boolean removeMemberFromGroup(String groupId, String memberToRemove, String operator) {
        GroupChat group = groupChats.get(groupId);
        if (group != null) {
            // 只有群主才能移除成员
            if (group.getCreator().equals(operator) && !memberToRemove.equals(group.getCreator())) {
                if (group.removeMember(memberToRemove)) {
                    removeFromUserGroup(memberToRemove, groupId);
                    return true;
                }
            } else if (operator.equals(memberToRemove)) { // 用户自己退出群聊
                if (group.removeMember(memberToRemove)) {
                    removeFromUserGroup(memberToRemove, groupId);
                    return true;
                }
            }
        }
        return false;
    }
    
    // 获取用户加入的所有群聊
    public static List<String> getUserGroups(String username) {
        List<String> groups = userGroups.get(username);
        if (groups == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(groups);
    }
    
    // 检查用户是否在某个群聊中
    public static boolean isUserInGroup(String username, String groupId) {
        GroupChat group = groupChats.get(groupId);
        if (group != null) {
            return group.isMember(username);
        }
        return false;
    }
    
    // 获取群聊成员列表
    public static List<String> getGroupMembers(String groupId) {
        GroupChat group = groupChats.get(groupId);
        if (group != null) {
            return new ArrayList<>(group.getMembers());
        }
        return new ArrayList<>();
    }
    
    // 获取所有群聊信息
    public static Collection<GroupChat> getAllGroups() {
        return groupChats.values();
    }
    
    // 辅助方法：将群聊添加到用户的群聊列表中
    private static void addToUserGroup(String username, String groupId) {
        userGroups.computeIfAbsent(username, k -> new ArrayList<>()).add(groupId);
    }
    
    // 辅助方法：从用户的群聊列表中移除群聊
    private static void removeFromUserGroup(String username, String groupId) {
        List<String> groups = userGroups.get(username);
        if (groups != null) {
            groups.remove(groupId);
            if (groups.isEmpty()) {
                userGroups.remove(username);
            }
        }
    }
}