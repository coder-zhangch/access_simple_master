package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.FailedData;
import com.example.entity.LatestData;
import com.example.mapper.FailedDataMapper;
import com.example.mapper.LatestDataMapper;
import com.example.service.FailedDataService;
import com.example.service.LatestDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FailedDataServiceImpl implements FailedDataService {

    @Autowired
    private FailedDataMapper failedDataMapper;

    @Override
    public List<FailedData> findByTime(String time) {
        if(StrUtil.isBlank(time)){
            return null;
        }
        return failedDataMapper.findByTime(time);
    }

    @Override
    public Boolean delete() {
        return failedDataMapper.delete();
    }

    @Override
    public Boolean deleteNotIn(List<String> idList) {
        return null;
    }

    @Override
    public Boolean insert(List<FailedData> list) {
        if(CollUtil.isEmpty(list)){
            return false;
        }
        return failedDataMapper.insert(list);
    }
}
