package com.itheima.hchat.service.impl;

import com.itheima.hchat.mapper.TbFriendMapper;
import com.itheima.hchat.mapper.TbFriendReqMapper;
import com.itheima.hchat.mapper.TbUserMapper;
import com.itheima.hchat.pojo.*;
import com.itheima.hchat.pojo.vo.FriendReq;
import com.itheima.hchat.service.FriendService;
import com.itheima.hchat.util.IdWorker;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: Kang Yong
 * @date: 2021/8/24 17:11
 * @version: v1.0
 */
@Service
@Transactional
public class FriendServiceImpl implements FriendService {

    @Autowired
    private TbFriendReqMapper friendReqMapper;

    @Autowired
    private TbFriendMapper friendMapper;

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void sendRequest(String fromUserid, String toUserid) {
        TbUser friend = userMapper.selectByPrimaryKey(toUserid);
        // 检查是否可以添加好友
        this.checkAllowToAddFriend(fromUserid, friend);

        // 入库
        TbFriendReq friendReq = new TbFriendReq();
        friendReq.setFromUserid(fromUserid);
        friendReq.setToUserid(toUserid);
        friendReq.setId(idWorker.nextId());
        friendReq.setStatus(0);
        friendReq.setCreatetime(new Date());
        friendReqMapper.insertSelective(friendReq);
    }

    @Override
    public List<FriendReq> findFriendReqByUserid(String userid) {
        // 根据用户id查询对应的好友请求
        TbFriendReqExample example = new TbFriendReqExample();
        TbFriendReqExample.Criteria criteria = example.createCriteria();
        criteria.andToUseridEqualTo(userid);
        criteria.andStatusEqualTo(0);

        List<TbFriendReq> tbFriendReqList = friendReqMapper.selectByExample(example);
        List<FriendReq> friendReqList = new ArrayList<>();

        // 根据好友请求，蒋发起好友请求的用户信息返回
        for (TbFriendReq tbFriendReq : tbFriendReqList) {
            TbUser tbUser = userMapper.selectByPrimaryKey(tbFriendReq.getFromUserid());
            FriendReq friendReq = new FriendReq();
            BeanUtils.copyProperties(tbUser, friendReq);
            friendReq.setId(tbFriendReq.getId());
            friendReqList.add(friendReq);
        }
        return friendReqList;
    }

    @Override
    public void acceptFriendReq(String reqid) {
        // 处理好友请求
        TbFriendReq friendReq = friendReqMapper.selectByPrimaryKey(reqid);
        friendReq.setStatus(1);
        friendReqMapper.updateByPrimaryKeySelective(friendReq);

        // 互相添加好友
        TbFriend friend1 = new TbFriend();
        friend1.setId(idWorker.nextId());
        friend1.setUserid(friendReq.getFromUserid());
        friend1.setFriendsId(friendReq.getToUserid());
        friend1.setCreatetime(new Date());

        TbFriend friend2 = new TbFriend();
        friend2.setId(idWorker.nextId());
        friend2.setUserid(friendReq.getToUserid());
        friend2.setFriendsId(friendReq.getFromUserid());
        friend2.setCreatetime(new Date());

        friendMapper.insert(friend1);
        friendMapper.insert(friend2);
    }

    @Override
    public void ignoreFriendReq(String reqid) {
        TbFriendReq friendReq = friendReqMapper.selectByPrimaryKey(reqid);
        // 设置为已处理
        friendReq.setStatus(1);
        friendReqMapper.updateByPrimaryKeySelective(friendReq);
    }


    private void checkAllowToAddFriend(String userid, TbUser friend) {
        // 1、用户不能添加自己为好友
        if (friend.getId().equals(userid)) {
            throw new RuntimeException("不能添加自己为好友");
        }
        // 2、用户不能重复添加
        // 如果用户已经是好友，不能重复添加
        TbFriendExample friendExample = new TbFriendExample();
        TbFriendExample.Criteria friendExampleCriteria = friendExample.createCriteria();
        friendExampleCriteria.andUseridEqualTo(userid);
        friendExampleCriteria.andFriendsIdEqualTo(friend.getId());
        List<TbFriend> friendList = friendMapper.selectByExample(friendExample);
        if (friendList != null && friendList.size() > 0) {
            throw new RuntimeException(friend.getUsername() + "已经是您的好友");
        }

        // 判断是否已经发送了好友申请，如果已经提交好友申请，
        TbFriendReqExample friendReqExample = new TbFriendReqExample();
        TbFriendReqExample.Criteria friendReqExampleCriteria = friendReqExample.createCriteria();
        friendReqExampleCriteria.andFromUseridEqualTo(userid);
        friendReqExampleCriteria.andToUseridEqualTo(friend.getId());
        friendReqExampleCriteria.andStatusEqualTo(0);
        List<TbFriendReq> friendReqList = friendReqMapper.selectByExample(friendReqExample);
        if (friendReqList != null && friendReqList.size() > 0) {
            throw new RuntimeException("已经申请过了");
        }
    }

}
