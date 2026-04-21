package com.example.controller;

import com.example.entity.WeighingData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.WeighingDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/WeighingData")
public class WeighingDataController {

    @Autowired
    private WeighingDataService weighingDataService;

    @GetMapping("/id/{id}")
    public WeighingData getById(@PathVariable("id")Long id){
        return weighingDataService.getById(id);
    }

}
