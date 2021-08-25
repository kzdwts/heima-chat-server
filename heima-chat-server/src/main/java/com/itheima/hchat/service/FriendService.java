package com.itheima.hchat.service;

import com.itheima.hchat.pojo.vo.FriendReq;
import com.itheima.hchat.pojo.vo.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: Kang Yong
 * @date: 2021/8/24 17:11
 * @version: v1.0
 */
public interface FriendService {

    /**
     * 新增好友请求记录
     *
     * @param fromUserid
     * @param toUserid
     */
    void sendRequest(String fromUserid, String toUserid);

    /**
     * 根据用户id查询他对应的好友请求
     *
     * @param userid 当前登录的用户
     * @return 请求好友的列表
     */
    List<FriendReq> findFriendReqByUserid(String userid);

    /**
     * 接受好友请求
     *
     * @param reqid
     */
    void acceptFriendReq(String reqid);

    /**
     * 忽略好友请求
     *
     * @param reqid 好友请求的id
     */
    void ignoreFriendReq(String reqid);
}
