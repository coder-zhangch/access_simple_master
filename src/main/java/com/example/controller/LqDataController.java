package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.entity.LqData;
import com.example.entity.WeighingData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.LqDataService;
import com.example.service.WeighingDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/LqData")
public class LqDataController {

    //缓存 LqData最后推送的数据id 的key
    private final static String LqData1_2LastPushId = "LqData1-2LastPushId:";
    private final static String LqData2_2LastPushId = "LqData2-2LastPushId:";
    private final static String LqData3_2LastPushId = "LqData3-2LastPushId:";

    @Autowired
    private LqDataService lqDataService;
    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/push")
    public void lqDataPush(){
        List<LqData> pushList = new ArrayList<>();
        pushList.add(lqDataService.getLastBy("1-2"));
        pushList.add(lqDataService.getLastBy("2-2"));
        pushList.add(lqDataService.getLastBy("3-2"));
        if(pushList.isEmpty()){
            return;
        }
        for (LqData lqData : pushList) {
            if (lqData == null) {
                continue;
            }
            try {
                lqDataService.dataPush(lqData);
                log.info("push success: {}", lqData.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                log.info("push fail: {}", lqData.getId());
            }
        }
    }

    @GetMapping("/pushByDate/{date}")
    public void lqDataPushByDate(@PathVariable("date")String date){
        List<LqData> pushList = new ArrayList<>();
        //日期的第二天
        DateTime dateTime = DateUtil.parseDate(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        String end = DateUtil.formatDate(calendar.getTime());
        pushList.add(lqDataService.getLastByDate("1-2", date, end));
        pushList.add(lqDataService.getLastByDate("2-2", date, end));
        pushList.add(lqDataService.getLastByDate("3-2", date, end));
        if(pushList.isEmpty()){
            return;
        }
        for (LqData lqData : pushList) {
            if (lqData == null) {
                continue;
            }
            try {
                lqDataService.dataPush(lqData);
                log.info("push success: {}", lqData.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
                e.printStackTrace();
                log.info("push fail: {}", lqData.getId());
            }
        }
    }
}
