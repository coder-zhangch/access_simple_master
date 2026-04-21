package com.example.controller;

import com.example.service.SaasDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/record")
public class RecordController {

    @Autowired
    private SaasDataService saasDataService;

    @GetMapping("/save/get")
    public int saveGetRecord(){
        return saasDataService.saveGetRecord();
    }
    @GetMapping("/save/send")
    public int saveSendRecord(){
        return saasDataService.saveSendRecord();
    }

    @GetMapping("/getAndSend")
    public String getAndSend(){
        try {
            saasDataService.getAndSend();
            return "流程结束";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
