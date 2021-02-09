package com.springboot.sample.bean;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;

}
