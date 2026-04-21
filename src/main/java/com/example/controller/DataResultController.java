package com.example.controller;

import com.example.service.DataResultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/DataResult")
public class DataResultController {

    @Autowired
    private DataResultService dataResultService;

    @GetMapping("/send")
    public String sendMessage(){
        dataResultService.sendMessage();
        return "执行结束:" + System.currentTimeMillis();
    }

    @GetMapping("/sendByQos/{qos}")
    public String sendMessage(@PathVariable("qos")Integer qos){
        dataResultService.sendByQos(qos);
        return "执行结束:" + System.currentTimeMillis();
    }

    @GetMapping("/sendByWare/{ware}")
    public String sendMessage(@PathVariable("ware")String ware){
        dataResultService.sendByWare(ware);
        return "执行结束:" + System.currentTimeMillis();
    }

    @GetMapping("/sendTest")
    public String sendTest(){
        dataResultService.sendTestMessage();
        return "执行结束:" + System.currentTimeMillis();
    }

    @GetMapping("/sendChart")
    public String sendToOfficialAccounts(){
        dataResultService.sendToOfficialAccounts();
        return "执行结束:" + System.currentTimeMillis();
    }

    @GetMapping("/qrcode/{deviceId}")
    public ResponseEntity<byte[]> getDeviceQrCode(@PathVariable String deviceId){
        String imageUrl = dataResultService.getDeviceQrCode(deviceId);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", imageUrl)
                .build();
    }
}
