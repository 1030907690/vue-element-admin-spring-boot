package com.springboot.sample.initialize;

import com.springboot.sample.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;

/***
 * @author zhouzhongqing
 * 初始化逻辑
 * 2021年2月10日11:40:52
 *
 **/
@Component
@Slf4j
public class ApplicationInitialize implements CommandLineRunner, EnvironmentAware {

    @Resource
    private ExecutorService executorService;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private Environment environment;

    @Value("${netty.config.ws.port}")
    private int wsPort;

    @Value("${netty.config.ws.path}")
    private String wsPath;

    @Override
    public void run(String... args) throws Exception {
        log.info("初始化程序");
        executorService.execute(() -> {
            try {
                new WebSocketServer().start(applicationContext,environment, wsPort, wsPath);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
