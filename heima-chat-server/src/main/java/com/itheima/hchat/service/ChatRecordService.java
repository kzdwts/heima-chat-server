package com.itheima.hchat.service;

import com.itheima.hchat.pojo.TbChatRecord;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 聊天记录处理业务接口层
 * @author: Kang Yong
 * @date: 2021/8/25 17:42
 * @version: v1.0
 */
public interface ChatRecordService {

    /**
     * 保存聊天记录信息
     *
     * @param tbChatRecord
     */
    void insert(TbChatRecord tbChatRecord);
}
