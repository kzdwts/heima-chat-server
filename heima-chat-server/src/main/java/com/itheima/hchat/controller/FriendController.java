package com.itheima.hchat.controller;

import com.itheima.hchat.pojo.TbFriendReq;
import com.itheima.hchat.pojo.vo.Result;
import com.itheima.hchat.pojo.vo.User;
import com.itheima.hchat.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description:
 * @author: Kang Yong
 * @date: 2021/8/24 15:09
 * @version: v1.0
 */
@RestController
@RequestMapping("/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * 发送好友请求
     *
     * @param friendReq
     * @return
     */
    @PostMapping("/sendRequest")
    public Result sendRequest(@RequestBody TbFriendReq friendReq) {
        try {
            friendService.sendRequest(friendReq.getFromUserid(), friendReq.getToUserid());
            return new Result(true, "已申请");
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false, e.getMessage());
        } catch (Exception e) {
            return new Result(false, e.getMessage());
        }
    }

    /**
     * 查看好友请求
     *
     * @param userid
     * @return
     */
    @GetMapping("/findFriendReqByUserid")
    public List<User> findFriendReqByUserid(String userid) {
        return friendService.findFriendReqByUserid(userid);
    }

    /**
     * 接受好友请求
     *
     * @param reqid
     * @return
     */
    @PostMapping("/acceptFriendReq")
    public Result acceptFriendReq(String reqid) {
        try {
            friendService.acceptFriendReq(reqid);
            return new Result(true, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "操作失败");
        }
    }

}
