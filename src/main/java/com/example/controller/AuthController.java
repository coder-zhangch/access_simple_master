package com.example.controller;

import com.example.common.StringUtil;
import com.example.request.BaseResult;
import com.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.AuthenticationException;
import java.util.Map;

/**
 * 权限认证相关的请求，这里的请求不需要被拦截
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/login")
    public BaseResult<String> login(@RequestBody Map<String, String> map){
        String username = map.get("username");
        String password = map.get("password");
        return authService.login(username, password);
    }
}
