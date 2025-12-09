package com.yychat.model;


import java.io.Serializable;
import java.util.Date;

public class Message implements Serializable, MessageType {
    String sender;
    String receiver;
    String content;
    String messageType;
    Date sendTime;
    String groupId; // 群聊ID
    String groupName; // 群聊名称
    String[] groupMembers; // 群聊成员列表
    
    // 文件传输相关属性
    String fileName;          // 文件名
    long fileSize;            // 文件大小
    byte[] fileData;          // 文件数据
    int chunkIndex;           // 当前数据块索引
    int totalChunks;          // 总数据块数
    String transferId;        // 文件传输唯一标识符
    
    // 用户状态相关属性
    String userStatus;        // 用户状态(在线/离线/忙碌等)
    String targetUser;        // 目标用户(用于查询特定用户状态)

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

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

    public String[] getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(String[] groupMembers) {
        this.groupMembers = groupMembers;
    }
    
    // 文件传输相关getter和setter方法
    
    /**
     * 获取文件名
     */
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件大小
     */
    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * 获取文件数据
     */
    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    /**
     * 获取当前数据块索引
     */
    public int getChunkIndex() {
        return chunkIndex;
    }

    public void setChunkIndex(int chunkIndex) {
        this.chunkIndex = chunkIndex;
    }

    /**
     * 获取总数据块数
     */
    public int getTotalChunks() {
        return totalChunks;
    }

    public void setTotalChunks(int totalChunks) {
        this.totalChunks = totalChunks;
    }

    /**
     * 获取文件传输唯一标识符
     */
    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }
    
    // 用户状态相关getter和setter方法
    
    /**
     * 获取用户状态
     */
    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    /**
     * 获取目标用户
     */
    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
        this.targetUser = targetUser;
    }
}
