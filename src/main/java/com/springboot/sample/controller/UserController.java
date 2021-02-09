package com.springboot.sample.controller;

import com.springboot.sample.bean.LoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class UserController {


    @PostMapping(value = "/vue-element-admin/user/login")
    public String login(@RequestBody LoginRequest loginRequest) {
        log.info("username {} password {}", loginRequest.getUsername(), loginRequest.getPassword());
        return "{\"code\":20000,\"data\":{\"token\":\"admin-token\"}}";
    }

    @GetMapping(value = "/vue-element-admin/user/info")
    public String info(String token) {
        log.info("token {}", token);
        return "{\"code\":20000,\"data\":{\"roles\":[\"admin\"],\"introduction\":\"I am a super administrator\",\"avatar\":\"https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif\",\"name\":\"Super Admin\"}}";
    }

    @PostMapping(value = "/vue-element-admin/user/logout")
    public String logout() {
        return "{\"code\":20000,\"data\":\"success\"}";
    }
}
