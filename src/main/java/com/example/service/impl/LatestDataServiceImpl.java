package com.example.service.impl;

import com.example.entity.LatestData;
import com.example.entity.ScaleData;
import com.example.mapper.LatestDataMapper;
import com.example.mapper.ScaleDataMapper;
import com.example.service.LatestDataService;
import com.example.service.ScaleDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LatestDataServiceImpl implements LatestDataService {

    @Autowired
    private LatestDataMapper latestDataMapper;

    @Override
    public LatestData findOne() {
        return latestDataMapper.findOne();
    }

    @Override
    public Boolean delete() {
        return latestDataMapper.delete();
    }

    @Override
    public Boolean insert(LatestData latestData) {
        if(latestData == null){
            return false;
        }
        return latestDataMapper.insert(latestData);
    }
}
