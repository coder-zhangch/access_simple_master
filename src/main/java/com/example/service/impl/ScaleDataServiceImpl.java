package com.example.service.impl;

import com.example.entity.ScaleData;
import com.example.entity.User;
import com.example.mapper.ScaleDataMapper;
import com.example.mapper.UserMapper;
import com.example.service.ScaleDataService;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScaleDataServiceImpl implements ScaleDataService {

    @Autowired
    private ScaleDataMapper scaleDataMapper;

    @Override
    public List<ScaleData> getById(String id) {
        return scaleDataMapper.getById(id);
    }
}
