package com.itheima.hchat.service;

import com.itheima.hchat.pojo.TbUser;
import com.itheima.hchat.pojo.vo.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: Kang Yong
 * @date: 2021/8/23 18:18
 * @version: v1.0
 */
public interface UserService {

    List<TbUser> findAlll();

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return
     */
    User login(String username, String password);

    /**
     * 注册新用户
     * 如果注册失败，则抛出异常
     *
     * @param user
     */
    void register(TbUser user);

    /**
     * 上传文件
     *
     * @param file
     * @param userid
     * @return
     */
    User upload(MultipartFile file, String userid);

    /**
     * 更新用户昵称
     *
     * @param id
     * @param nickname
     */
    void updateNickname(String id, String nickname);

    /**
     * 根据用户id查询用户信息
     *
     * @param userid
     * @return
     */
    User findById(String userid);

    /**
     * 根据用户id查询用户信息
     *
     * @param userid
     * @param friendUsername
     * @return
     */
    User findByUsername(String userid, String friendUsername);
}
