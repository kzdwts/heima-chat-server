package com.itheima.hchat.service;

import com.itheima.hchat.pojo.TbChatRecord;

import java.util.List;

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

    /**
     * 查询和好友聊天记录数据
     *
     * @param userid
     * @param friendid
     * @return
     */
    List<TbChatRecord> findByUseridAndFriendid(String userid, String friendid);

    /**
     * 查询未读消息
     *
     * @param userid
     * @return
     */
    List<TbChatRecord> findUnreadByUserid(String userid);

    /**
     * 更新消息状态为已读
     *
     * @param id
     */
    void updateStatusHasRead(String id);
}
