package com.example.controller;

import com.example.entity.ScaleData;
import com.example.entity.User;
import com.example.service.ScaleDataService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/ScaleData")
public class ScaleDataController {

    @Autowired
    private ScaleDataService scaleDataService;

    @GetMapping("/id/{id}")
    public List<ScaleData> getById(@PathVariable("id")String id){
        return scaleDataService.getById(id);
    }
}
