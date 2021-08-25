package com.itheima.hchat.netty;

import io.netty.channel.Channel;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: 建立用户与channel通道的关系
 * @author: Kang Yong
 * @date: 2021/8/25 16:22
 * @version: v1.0
 */
public class UserChannelMap {
    // 保存用户id与channel通道关系
    private static Map<String, Channel> userChannelMap;

    static {
        userChannelMap = new HashMap<String, Channel>();
    }

    /**
     * 添加用户id与channel的关系
     *
     * @param userid
     * @param channel
     */
    public static void put(String userid, Channel channel) {
        userChannelMap.put(userid, channel);
    }

    /**
     * 根据用户id，移除用户id与channel的关系
     *
     * @param userid
     */
    public static void remove(String userid) {
        userChannelMap.remove(userid);
    }

    /**
     * 根据通道的id，移除与用户的关联
     *
     * @param channelId
     */
    public static void removeByChannelId(String channelId) {
        if (StringUtils.isBlank(channelId)) {
            return;
        }
        // 移除关联
        for (String s : userChannelMap.keySet()) {
            if (channelId.equals(userChannelMap.get(s).id().asLongText())) {
                System.out.println("客户端连接断开，取消用户：" + s + " 与通道的关联");
                userChannelMap.remove(s);
                break;
            }
        }
    }

    /**
     * 打印所有的用户与通道的关联关系
     */
    public static void print() {
        for (String s : userChannelMap.keySet()) {
            System.out.println("用户id：" + s + " 通道：" + userChannelMap.get(s).id());
        }
    }

}
