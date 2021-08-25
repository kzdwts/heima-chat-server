package com.itheima.hchat.service.impl;

import com.itheima.hchat.mapper.TbChatRecordMapper;
import com.itheima.hchat.pojo.TbChatRecord;
import com.itheima.hchat.service.ChatRecordService;
import com.itheima.hchat.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

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

}
