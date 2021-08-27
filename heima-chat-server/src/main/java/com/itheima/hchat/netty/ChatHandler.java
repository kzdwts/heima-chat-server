package com.itheima.hchat.netty;

import com.alibaba.fastjson.JSON;
import com.itheima.hchat.pojo.TbChatRecord;
import com.itheima.hchat.service.ChatRecordService;
import com.itheima.hchat.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;

/**
 * 处理消息的handler
 * TextWebSocketFrame: 在netty中，是用于为websocket专门处理文本的对象，frame是消息的载体
 */
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    // 用来保存所有的客户端连接
    private static ChannelGroup clients = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:MM");

    /**
     * 当channel中有新的事件消息会自动调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 当接收到数据后，会自动调用

        // 获取客户端发送过来的文本消息
        String text = msg.text();
        System.out.println("接收到的消息数据为：" + text);

        // 处理消息
        Message message = JSON.parseObject(text, Message.class);

        // 通过工具类获取容器里面的业务接口
        ChatRecordService chatRecordService = SpringUtil.getBean(ChatRecordService.class);

        switch (message.getType()) {
            // 处理客户端连接的消息
            case 0:
                // 建立连接
                String userid = message.getChatRecord().getUserid();
                UserChannelMap.put(userid, ctx.channel());
                System.out.println("建立用户：" + userid + " 与通道：" + ctx.channel().id() + "的关联");
                UserChannelMap.print();
                break;
            case 1:
                // 将聊天消息保存到数据库
                TbChatRecord chatRecord = message.getChatRecord();
                chatRecordService.insert(chatRecord);

                // 1.如果发送消息好友在线，可以直接发送消息
                Channel friendChannel = UserChannelMap.get(chatRecord.getFriendid());
                if (friendChannel != null) {
                    friendChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(message)));
                } else {
                    // 2.如果不在线，暂时不发送
                    System.out.println("用户：" + chatRecord.getFriendid() + "不在线");
                }
                break;
            // 前端消息已读（签收）
            case 2:
                // 更新未读消息记录为已读
                chatRecordService.updateStatusHasRead(message.getChatRecord().getId());
                break;
            case 3:
                System.out.println("接收到心跳消息:" + JSON.toJSONString(message));
                break;
        }

    }

    /**
     * 当有新的客户端连接到服务器之后，会自动调用这个方法
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 将新的通道加入到clients
        clients.add(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        ctx.close();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("关闭通道");
        UserChannelMap.removeByChannelId(ctx.channel().id().asLongText());
        ctx.close();
    }
}
