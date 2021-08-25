package com.itheima.hchat.netty;

import com.itheima.hchat.pojo.TbChatRecord;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 聊天消息体
 * @author: Kang Yong
 * @date: 2021/8/25 16:12
 * @version: v1.0
 */
public class Message {

    private String type; // 消息类型
    private TbChatRecord chatRecord; // 聊天消息
    private Object ext; // 扩展内容

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TbChatRecord getChatRecord() {
        return chatRecord;
    }

    public void setChatRecord(TbChatRecord chatRecord) {
        this.chatRecord = chatRecord;
    }

    public Object getExt() {
        return ext;
    }

    public void setExt(Object ext) {
        this.ext = ext;
    }
}
