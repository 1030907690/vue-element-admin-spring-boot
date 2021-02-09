package com.springboot.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;

@SpringBootApplication
public class SampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleApplication.class, args);
	}
	/**
	 * 限制上传文件大小
	 */
	@Bean
	public MultipartConfigElement multipartConfigElement(){
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//单个文件最大 5m 可以使用读取配置
		factory.setMaxFileSize("9120KB"); //KB,MB
		/// 设置总上传数据总大小 50m
		factory.setMaxRequestSize("512000KB");
		return factory.createMultipartConfig();
	}

}
