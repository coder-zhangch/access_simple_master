package com.example.controller;

import com.example.entity.SumData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.SumDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/SumData")
public class SumDataController {

    @Autowired
    private SumDataService sumDataService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/id/{id}")
    public SumData getById(@PathVariable("id")String id){
        return sumDataService.getById(id);
    }

    @GetMapping("/push/{id}")
    public BaseResult pushData(@PathVariable("id") String id){
        SumData sumData = sumDataService.getById(id);
        if(sumData == null){
            log.info("手动数据推送：id{}数据查询为null", id);
        }
        log.info("手动数据推送：查询对象{}", sumData);
        BaseResult<String> result = null;
        try {
            result = HttpsRequest.pushData("手动数据推送", sumData);
        } catch (Exception e) {
            log.info("手动数据推送异常");
            throw new RuntimeException(e);
        }
        return result;
    }

    //给Redis设置key
    @GetMapping("/key/{key}/{value}")
    public String setKey(@PathVariable("key")String key, @PathVariable("value") String value){
        redisTemplate.boundValueOps(key).set(value);
        return redisTemplate.boundValueOps(key).get().toString();
    }
}
