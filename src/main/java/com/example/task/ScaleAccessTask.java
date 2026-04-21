package com.example.task;

import com.example.service.DataResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScaleAccessTask {

    @Autowired
    private DataResultService dataResultService;

//    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    //每天9点半执行一次
    @Async
    @Scheduled(cron = "0 30 9 * * ? ")
    public void run9_30(){
        dataResultService.sendMessage();
    }
    //每天12点半执行一次
    @Async
    @Scheduled(cron = "0 30 12 * * ? ")
    public void run12_30(){
        dataResultService.sendMessage();
    }
    //每天18点半执行一次
    @Async
    @Scheduled(cron = "0 30 18 * * ? ")
    public void run18_30(){
        dataResultService.sendMessage();
    }
}
