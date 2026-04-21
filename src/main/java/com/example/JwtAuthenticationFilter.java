package com.example;

import com.example.common.StringUtil;
import com.example.common.TokenUtil;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //从请求头中获取token
        String authHeader = request.getHeader(TokenUtil.header);
        String username = TokenUtil.getUsernameFromToken(authHeader);
        //校验token
        if(StringUtil.username.equals(username)){
            //校验token的合法性
            if(TokenUtil.validateToken(authHeader, username)){
                filterChain.doFilter(request, response);
            }else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "身份验证异常，请求失败！");
            }
        }else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "身份验证异常，请求失败！");
        }
    }
}
