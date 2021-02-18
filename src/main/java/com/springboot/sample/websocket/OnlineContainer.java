package com.springboot.sample.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * @author Zhou Zhong Qing
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: 存储在线ws用户的容器
 * @date 2019/4/16 20:50
 */
@Component
@Slf4j
public class OnlineContainer {

    @Resource
    private ExecutorService executorService;

    /**
     * <session,ChannelHandlerContext>
     **/
    private final Map<String, ChannelHandlerContext> onlineUserMap = new ConcurrentHashMap<>();

    /**
     * <userId,sessionId>
     **/
    private final Map<String, String> userMap = new ConcurrentHashMap<>();

    public void addOnlineUser(String userId, ChannelHandlerContext ctx) {
        String longId = ctx.channel().id().asLongText();
        userMap.put(userId, longId);
        onlineUserMap.put(longId, ctx);
        log.info("用户 [ {} ] 上线", userId);
    }

    public String removeUserBySessionId(String sessionId) {
        //如果存在则删除
        String key = null;
        if (userMap.containsValue(sessionId)) {

            for (Map.Entry<String, String> entry : userMap.entrySet()) {
                if (null != entry.getValue() && entry.getValue().equals(sessionId)) {
                    key = entry.getKey();
                    break;
                }
            }
            if (null != key) {
                log.info("用户 [ {} ] 离线 ", key);
                userMap.remove(key);
            }
            onlineUserMap.remove(sessionId);
        }
        return key;
    }

    public Map<String, String> getUserMap() {
        return userMap;
    }

    public Map<String, ChannelHandlerContext> getOnlineUserMap() {
        return onlineUserMap;
    }

    @PostConstruct
    public void broadcastHeartBeat() {
        executorService.execute(()->{
            while (true){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onlineUserMap.forEach((k,v)->{
                    String message = "broadcastHeartBeat" + System.currentTimeMillis();
                    v.channel().writeAndFlush(new TextWebSocketFrame("{\"msg\":\""+message+"\"}"));
                });

            }
        });
    }
}