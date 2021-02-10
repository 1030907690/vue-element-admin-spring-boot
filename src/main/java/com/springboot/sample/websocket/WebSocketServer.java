package com.springboot.sample.websocket;

import com.springboot.sample.websocket.handler.TextWebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import io.netty.channel.ChannelInitializer;
import org.springframework.core.env.Environment;

/**
 * @author zhouzhongqing
 * websocket服务
 * 2021年2月10日11:26:38
 */
@Slf4j
public class WebSocketServer {

    private final int AVAILABLEP_ROCESSORS = Runtime.getRuntime().availableProcessors();

    public void start(ApplicationContext applicationContext, Environment environment, int port, String path) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup(AVAILABLEP_ROCESSORS);
        EventLoopGroup workGroup = new NioEventLoopGroup(AVAILABLEP_ROCESSORS);
        //专门处理业务的线程组
        EventLoopGroup businessGroup = new NioEventLoopGroup(AVAILABLEP_ROCESSORS);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.TRACE))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LoggingHandler(LogLevel.TRACE))
                                    // HttpRequestDecoder和HttpResponseEncoder的一个组合，针对http协议进行编解码
                                    .addLast(new HttpServerCodec())
                                    // 分块向客户端写数据，防止发送大文件时导致内存溢出， channel.write(new ChunkedFile(new File("bigFile.mkv")))
                                    .addLast(new ChunkedWriteHandler())
                                    // 将HttpMessage和HttpContents聚合到一个完成的 FullHttpRequest或FullHttpResponse中,具体是FullHttpRequest对象还是FullHttpResponse对象取决于是请求还是响应
                                    // 需要放到HttpServerCodec这个处理器后面
                                    .addLast(new HttpObjectAggregator(10240))
                                    // webSocket 数据压缩扩展，当添加这个的时候WebSocketServerProtocolHandler的第三个参数需要设置成true
                                    .addLast(new WebSocketServerCompressionHandler())
                                    // 自定义处理器 - 处理 web socket 文本消息
                                    .addLast(businessGroup, new TextWebSocketHandler(applicationContext, environment))
                                    // 自定义处理器 - 处理 web socket 二进制消息
                                    //.addLast(new BinaryWebSocketFrameHandler())
                                    // 服务器端向外暴露的 web socket 端点，当客户端传递比较大的对象时，maxFrameSize参数的值需要调大
                                    .addLast(new WebSocketServerProtocolHandler(path, null, true, 910485760));

                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("webSocket server listen on port : [{}]", port);
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
