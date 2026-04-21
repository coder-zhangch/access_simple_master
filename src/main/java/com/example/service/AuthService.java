package com.example.service;

import com.example.request.BaseResult;

public interface AuthService {

    //登录，返回token
    BaseResult<String> login(String username, String password);
}
