package com.springboot.sample.cross;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author pn20120162
 */
@Slf4j
@Component
public class CrossFilter implements Filter {


    private String vueCrossConfig = "localhost";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 来源-域
        String originHeader = request.getHeader("Origin");
        //log.info("originHeader [ {} ] vueCrossConfig [ {} ] ",originHeader , vueCrossConfig);
      /*  if (null != originHeader) {
            if (vueCrossConfig.indexOf(originHeader) > -1) {
                response.setHeader("Access-Control-Allow-Origin", originHeader);
            }
        }*/
        response.setHeader("Access-Control-Allow-Origin", originHeader);
        response.setHeader("Access-Control-Allow-Headers", "X-Requested-With, Content-Type, X-User-Login, content-type, Access-Token,Token");//这里“Token”是我要传到后台的内容key
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");


        filterChain.doFilter(servletRequest, servletResponse);
    }

}