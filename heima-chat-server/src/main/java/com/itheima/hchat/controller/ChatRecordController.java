package com.itheima.hchat.controller;

import com.itheima.hchat.pojo.TbChatRecord;
import com.itheima.hchat.service.ChatRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: Kang Yong
 * @date: 2021/8/25 18:08
 * @version: v1.0
 */
@RestController
@RequestMapping("/chatRecord")
public class ChatRecordController {

    @Autowired
    private ChatRecordService chatRecordService;

    /**
     * 加载聊天记录
     *
     * @param userid   当前用户id
     * @param friendid 好友id
     * @return
     */
    @GetMapping("/findByUseridAndFriendid")
    public List<TbChatRecord> findByUseridAndFriendid(String userid, String friendid) {
        try {
            return chatRecordService.findByUseridAndFriendid(userid, friendid);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<TbChatRecord>();
        }
    }
}
