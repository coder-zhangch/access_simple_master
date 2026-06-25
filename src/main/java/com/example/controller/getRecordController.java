package com.example.controller;

import com.example.request.BaseResult;
import com.example.service.GetRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/getrecord")
public class getRecordController {

    @Autowired
    private GetRecordService getRecordService;

    @GetMapping("/history/{date}")
    public BaseResult<String> sendHistory(@PathVariable("date") String date){
        String message = getRecordService.sendHistory(date);
        return BaseResult.ok(message);
    }
}
