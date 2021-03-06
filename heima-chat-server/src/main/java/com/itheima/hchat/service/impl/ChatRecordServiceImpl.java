package com.itheima.hchat.service.impl;

import com.itheima.hchat.mapper.TbChatRecordMapper;
import com.itheima.hchat.pojo.TbChatRecord;
import com.itheima.hchat.pojo.TbChatRecordExample;
import com.itheima.hchat.service.ChatRecordService;
import com.itheima.hchat.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 聊天记录处理业务实现层
 * @author: Kang Yong
 * @date: 2021/8/25 17:42
 * @version: v1.0
 */
@Service
public class ChatRecordServiceImpl implements ChatRecordService {

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbChatRecordMapper chatRecordMapper;

    @Override
    public void insert(TbChatRecord chatRecord) {
        chatRecord.setId(idWorker.nextId());
        chatRecord.setHasRead(0);
        chatRecord.setHasDelete(0);
        chatRecord.setCreatetime(new Date());
        chatRecordMapper.insert(chatRecord);
    }

    @Override
    public List<TbChatRecord> findByUseridAndFriendid(String userid, String friendid) {
        TbChatRecordExample example = new TbChatRecordExample();
        // 我发送的聊天消息
        TbChatRecordExample.Criteria criteria1 = example.createCriteria();
        criteria1.andUseridEqualTo(userid);
        criteria1.andFriendidEqualTo(friendid);
        criteria1.andHasDeleteEqualTo(0);

        // 好友发送的聊天消息
        TbChatRecordExample.Criteria criteria2 = example.createCriteria();
        criteria2.andUseridEqualTo(friendid);
        criteria2.andFriendidEqualTo(userid);
        criteria2.andHasDeleteEqualTo(0);

        // 用or连接
        example.or(criteria1);
        example.or(criteria2);

        // 更新未读为已读
        TbChatRecordExample exampleQuerySendToMe = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteriaQuerySendToMe = exampleQuerySendToMe.createCriteria();
        criteriaQuerySendToMe.andFriendidEqualTo(userid);
        List<TbChatRecord> tbChatRecords = chatRecordMapper.selectByExample(exampleQuerySendToMe);
        for (TbChatRecord record : tbChatRecords) {
            record.setHasRead(1);
            chatRecordMapper.updateByPrimaryKey(record);
        }

        return chatRecordMapper.selectByExample(example);
    }

    @Override
    public List<TbChatRecord> findUnreadByUserid(String userid) {
        TbChatRecordExample example = new TbChatRecordExample();
        TbChatRecordExample.Criteria criteria = example.createCriteria();
        criteria.andFriendidEqualTo(userid);

        List<TbChatRecord> chatRecordList = chatRecordMapper.selectByExample(example);
        return chatRecordList;
    }

    @Override
    public void updateStatusHasRead(String id) {
        TbChatRecord chatRecord = new TbChatRecord();
        chatRecord.setId(id);
        chatRecord.setHasRead(1);
        chatRecordMapper.updateByPrimaryKey(chatRecord);
    }

}
