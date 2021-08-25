package com.itheima.hchat.service.impl;

import com.itheima.hchat.mapper.TbFriendMapper;
import com.itheima.hchat.mapper.TbFriendReqMapper;
import com.itheima.hchat.mapper.TbUserMapper;
import com.itheima.hchat.pojo.*;
import com.itheima.hchat.pojo.vo.User;
import com.itheima.hchat.service.UserService;
import com.itheima.hchat.util.FastDFSClient;
import com.itheima.hchat.util.IdWorker;
import com.itheima.hchat.util.QRCodeUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: Kang Yong
 * @date: 2021/8/23 18:18
 * @version: v1.0
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private TbUserMapper userMapper;

    @Autowired
    private TbFriendMapper friendMapper;

    @Autowired
    private TbFriendReqMapper friendReqMapper;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private FastDFSClient fastDFSClient;

    @Autowired
    private Environment env;

    @Autowired
    private QRCodeUtils qrCodeUtils;

    @Override
    public List<TbUser> findAlll() {
        return userMapper.selectByExample(null);
    }

    @Override
    public User login(String username, String password) {
        // 查询
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<TbUser> userList = userMapper.selectByExample(example);

        if (userList != null && userList.size() > 0) {
            String encodingPassword = DigestUtils.md5DigestAsHex(password.getBytes());
            if (encodingPassword.equals(userList.get(0).getPassword())) {
                User userResult = new User();
                BeanUtils.copyProperties(userList.get(0), userResult);
                return userResult;
            }
        }

        return null;
    }

    @Override
    public void register(TbUser user) {
        // 1、查询用户是否存在
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(user.getUsername());
        List<TbUser> userList = userMapper.selectByExample(example);

        // 1.1、如果存在抛出异常
        if (userList != null && userList.size() > 0) {
            throw new RuntimeException("用户名已存在");
        }
        // 2、将用户信息保存到数据库
        user.setId(idWorker.nextId());
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        user.setPicSmall("");
        user.setPicNormal("");
        user.setNickname(user.getUsername());

        // 生成二维码，并且将路径保存到数据库中
        try {
            String qrcodeStr = "hichat://" + user.getUsername();
            String tempDir = env.getProperty("hcat.tmpdir");
            String qrCodeFilePath = tempDir + user.getUsername() + ".png";
            qrCodeUtils.createQRCode(qrCodeFilePath, qrcodeStr);
            // 保存到fdfs

            String url = env.getProperty("fdfs.httpurl") + fastDFSClient.uploadFile(new File(qrCodeFilePath));
            user.setQrcode(url);


            user.setCreatetime(new Date());

            userMapper.insert(user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User upload(MultipartFile file, String userid) {
        try {
            String url = fastDFSClient.uploadFace(file);
            String suffix = "_150x150.";
            String[] pathList = url.split("\\.");
            String fileName = pathList[0];
            String ext = pathList[1];
            String picSmallUrl = fileName + suffix + ext;
            String prefix = env.getProperty("fdfs.httpurl");

            // 设置头像大图片
            TbUser tbUser = userMapper.selectByPrimaryKey(userid);
            tbUser.setPicNormal(prefix + url);
            // 设置头像小图片
            tbUser.setPicSmall(prefix + picSmallUrl);
            // 将新的头像更新到数据库
            userMapper.updateByPrimaryKeySelective(tbUser);

            // 将用户信息返回
            User user = new User();
            BeanUtils.copyProperties(tbUser, user);
            return user;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateNickname(String id, String nickname) {
        TbUser user = userMapper.selectByPrimaryKey(id);
        user.setNickname(nickname);
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public User findById(String userid) {
        TbUser tbUser = userMapper.selectByPrimaryKey(userid);
        User user = new User();
        BeanUtils.copyProperties(tbUser, user);
        return user;
    }

    @Override
    public User findByUsername(String userid, String friendUsername) {
        // 查询朋友信息
        TbUserExample example = new TbUserExample();
        TbUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(friendUsername);
        List<TbUser> userList = userMapper.selectByExample(example);
        TbUser friend = userList.get(0);
        checkAllowToAddFriend(userid, friendUsername, friend);

        // 返回
        User friendUser = new User();
        BeanUtils.copyProperties(friend, friendUser);
        return friendUser;
    }

    private void checkAllowToAddFriend(String userid, String friendUsername, TbUser friend) {
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
            throw new RuntimeException(friendUsername + "已经是您的好友");
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
