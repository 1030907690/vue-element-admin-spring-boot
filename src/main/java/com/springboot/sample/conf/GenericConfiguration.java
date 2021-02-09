package com.springboot.sample.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.*;

@Configuration
public class GenericConfiguration {
    private final int processors = Runtime.getRuntime().availableProcessors();

    @Bean
    public ExecutorService executorService(){
        ExecutorService executorService = new ThreadPoolExecutor(processors * 2, processors * 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(processors * 100), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
        return executorService;
    }


}
