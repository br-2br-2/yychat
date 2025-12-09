package com.yychat.model;

public interface MessageType {
    String LOGIN_VALLDATE_SUCCESS = "1";
    String LOGIN_VALLDATE_FALLURE = "2";
    String COMMON_CHAT_MESSAGE = "3";
    String REQUEST_ONLINE_FRIEND="4";
    String RESPONSE_ONLINE_FRIEND="5";
    String NEW_ONLINE_TO_ALL_FRIEND="6";
    String NEW_ONLINE_FRIEND="7";
    String USER_REGISTER_SUCCESS = "8";
    String USER_REGISTER_FAILURE="9";
    String ADD_NEW_FRIEND = "10";
    String ADD_NEW_FRIEND_FALLURE_NO_USER="11";
    String ADD_NEW_FRIEND_FALLURE_ALREADY_FRIEND="12";
    String ADD_NEW_FRIEND_SUCCESS="13";
    String USER_EXIT_SERVER_THREAD_CLOSE = "14";
    String USER_EXIT_CLIENT_THREAD_CLOSE = "15";
    String CREATE_GROUP_CHAT = "16";
    String JOIN_GROUP_CHAT = "17";
    String LEAVE_GROUP_CHAT = "18";
    String GROUP_CHAT_MESSAGE = "19";
    String DELETE_FRIEND = "20";
    String BLACKLIST_FRIEND = "21";
    String REMOVE_BLACKLIST = "22";
    
    // 文件传输相关消息类型
    String FILE_TRANSFER_REQUEST = "23";      // 文件传输请求
    String FILE_TRANSFER_RESPONSE = "24";     // 文件传输响应
    String FILE_TRANSFER_DATA = "25";         // 文件数据传输
    String FILE_TRANSFER_COMPLETE = "26";     // 文件传输完成
    String FILE_TRANSFER_CANCEL = "27";       // 文件传输取消
    
    // 用户状态相关消息类型
    String USER_STATUS_UPDATE = "28";         // 用户状态更新
    String REQUEST_USER_STATUS = "29";        // 请求用户状态
    String RESPONSE_USER_STATUS = "30";       // 响应用户状态
}
