package com.example.service.impl;

import com.example.common.StringUtil;
import com.example.common.TokenUtil;
import com.example.request.BaseResult;
import com.example.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = RuntimeException.class)
public class AuthServiceImpl implements AuthService {

    @Override
    public BaseResult<String> login(String username, String password) {
        if(!(StringUtil.username.equals(username) && StringUtil.password.equals(password))){
            return BaseResult.fail("账号或密码错误，登录失败！");
        }
        String token = TokenUtil.generateToken(username);
        return BaseResult.ok(token);
    }
}
