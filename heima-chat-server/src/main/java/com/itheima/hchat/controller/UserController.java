package com.itheima.hchat.controller;

import com.itheima.hchat.pojo.TbUser;
import com.itheima.hchat.pojo.vo.Result;
import com.itheima.hchat.pojo.vo.User;
import com.itheima.hchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: Kang Yong
 * @date: 2021/8/23 18:16
 * @version: v1.0
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/findAll")
    public List<TbUser> findAll() {
        return userService.findAlll();
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    @PostMapping("/login")
    public Result login(@RequestBody TbUser user) {
        try {
            User userResult = userService.login(user.getUsername(), user.getPassword());
            if (userResult == null) {
                return new Result(false, "登录失败，请检查用户名和密码");
            } else {
                return new Result(true, "登录成功", userResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "登录错误");
        }
    }

    @PostMapping("/register")
    public Result register(@RequestBody TbUser user) {
        try {
            userService.register(user);
            return new Result(true, "注册成功");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, "注册失败");
        }
    }

    /**
     * 上传文件
     *
     * @param file
     * @param userid
     * @return
     */
    @PostMapping("/upload")
    public Result upload(MultipartFile file, String userid) {
        try {
            User user = userService.upload(file, userid);

            if (user != null) {
                return new Result(true, "成功", user);
            } else {
                return new Result(false, "上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "错误");
        }
    }

    /**
     * 更新用户昵称
     *
     * @param user
     * @return
     */
    @PutMapping("/updateNickname")
    public Result updateNickname(@RequestBody TbUser user) {
        try {
            userService.updateNickname(user.getId(), user.getNickname());
            return new Result(true, "成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "错误");
        }
    }

    /**
     * 根据id查询用户信息
     *
     * @param userid
     * @return
     */
    @GetMapping("/findById")
    public User findById(String userid) {
        return userService.findById(userid);
    }

    /**
     * 根据id搜索用户
     *
     * @param userid
     * @return
     */
    @GetMapping("/findByUsername")
    public User findByUsername(String userid, String friendUsername) {
        try {
            return userService.findByUsername(userid, friendUsername);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
