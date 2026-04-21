package com.example.controller;

import com.example.entity.WeighingData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.WeighingDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Slf4j
@RestController
@RequestMapping("/WeighingData")
public class WeighingDataController {

    @Autowired
    private WeighingDataService weighingDataService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/id/{EntryId}")
    public WeighingData getById(@PathVariable("id")String EntryId){
        return weighingDataService.getById(EntryId);
    }

    @GetMapping("/push/{EntryId}")
    public BaseResult pushData(@PathVariable("EntryId") String EntryId){
        WeighingData weighingData = weighingDataService.getById(EntryId);
        if(weighingData == null){
            log.info("Operation Data Push：EntryId{} is null", EntryId);
        }
        log.info("Operation Data Push：Object{}", weighingData);
        BaseResult<String> result = null;
        try {
            result = HttpsRequest.pushData("Operation Data Push", weighingData);
        } catch (Exception e) {
            log.info("Operation Data Push Failure");
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

    //获取Redis的key对应的值
    @GetMapping("/value/{key}")
    public String getValue(@PathVariable("key")String key){
        return redisTemplate.boundValueOps(key).get().toString();
    }

    //将指定数据推送，并赋值指定的重量
    @GetMapping("/selfPush/{targetId}/{changeId}/{weight}")
    public BaseResult pushData(
            @PathVariable("targetId") String targetId,
            @PathVariable("changeId") String changeId,
            @PathVariable("weight") String weight){
        WeighingData weighingData = weighingDataService.getById(targetId);
        if(weighingData == null){
            log.info("Operation Data Push：id{} is null", targetId);
        }
        weighingData.setTotalWeight(new BigDecimal(weight));
        weighingData.setEntryID(Long.valueOf(changeId));
        log.info("Operation Data Push：Object{}", weighingData);
        BaseResult<String> result = null;
        try {
            result = HttpsRequest.pushData("Operation Data Push", weighingData);
        } catch (Exception e) {
            log.info("Operation Data Push Failure");
            throw new RuntimeException(e);
        }
        return result;
    }
}
