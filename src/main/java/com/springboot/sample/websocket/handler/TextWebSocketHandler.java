package com.springboot.sample.websocket.handler;

import com.springboot.sample.websocket.OnlineContainer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Date;
import java.util.concurrent.ExecutorService;

/**
 * @author Zhou Zhong Qing
 * @Title: ${file_name}
 * @Package ${package_name}
 * @Description: 文本消息处理
 * @date 2019/4/16 17:29
 */
@Slf4j
public class TextWebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private ExecutorService executorService;

    private ApplicationContext applicationContext;

    private Environment environment;

    private String defaultWebSocketLink;

    private OnlineContainer onlineContainer;

    public TextWebSocketHandler(ApplicationContext applicationContext, Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
        this.executorService = applicationContext.getBean(ExecutorService.class);
        this.defaultWebSocketLink = environment.getProperty("netty.config.ws.path");
        this.onlineContainer = applicationContext.getBean(OnlineContainer.class);
    }

    /*
    经过测试，在 ws 的 uri 后面不能传递参数，不然在 netty 实现 websocket 协议握手的时候会出现断开连接的情况。
   针对这种情况在 websocketHandler 之前做了一层 httpHander 过滤，将传递参数放入 channel 的 attr 中，然后重写
   request 的 uri，并传入下一个管道中，基本上解决了这个问题。
    * */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        initConnection(ctx,msg);
        super.channelRead(ctx, msg);
    }

    private void initConnection(ChannelHandlerContext ctx, Object msg){
        if (null != msg && msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            log.info("调用 channelRead request.uri() [ {} ]", request.uri());
            String uri = request.uri();
            if (null != uri && uri.contains(defaultWebSocketLink) && uri.contains("?")) {
                String[] uriArray = uri.split("\\?");
                if (null != uriArray && uriArray.length > 1) {
                    String[] paramsArray = uriArray[1].split("=");
                    if (null != paramsArray && paramsArray.length > 1) {
                        onlineContainer.addOnlineUser(paramsArray[1], ctx);
                    }
                }
            }
            request.setUri(defaultWebSocketLink);
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        log.info("接收到客户端的消息:[{}]", msg.text());


    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("服务器发生了异常: [ {} ]", cause);
    }


    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("web socket 握手成功。" + ctx.channel().id().asLongText());
            WebSocketServerProtocolHandler.HandshakeComplete handshakeComplete = (WebSocketServerProtocolHandler.HandshakeComplete) evt;
            String requestUri = handshakeComplete.requestUri();
            log.info("requestUri:[{}]", requestUri);
            String subproTocol = handshakeComplete.selectedSubprotocol();
            log.info("subproTocol:[{}]", subproTocol);
            handshakeComplete.requestHeaders().forEach(entry -> log.info("header key:[{}] value:[{}]", entry.getKey(), entry.getValue()));
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 添加
        //log.info(" 客户端加入 [ {} ]", ctx.channel().id().asLongText());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 移除
        log.info(" 离线 [ {} ] ", ctx.channel().id().asLongText());
        super.channelInactive(ctx);
        onlineContainer.removeUserBySessionId(ctx.channel().id().asLongText());
        ctx.close();
    }

}