package com.example;

import com.example.entity.User;
import com.example.service.impl.UserServiceImpl;
import org.apache.catalina.core.ApplicationContext;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

/**
 * @author zch
 * @date 2024/4/27 20:42
 */
@EnableAsync
@EnableScheduling //开启定时任务
@MapperScan(basePackages = {"com.example.mapper"})
@SpringBootApplication
public class AccessSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccessSpringBootApplication.class, args);
//        ConfigurableApplicationContext context = SpringApplication.run(AccessSpringBootApplication.class, args);
//        UserServiceImpl bean = context.getBean(UserServiceImpl.class);
//        List<User> list = bean.findAll();
//        System.out.println(list);
    }
}
