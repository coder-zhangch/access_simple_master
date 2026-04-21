package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.WeighingData;
import com.example.mapper.WeighingDataMapper;
import com.example.service.WeighingDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeighingDataServiceImpl implements WeighingDataService {

    @Autowired
    private WeighingDataMapper weighingDataMapperMapper;

    @Override
    public WeighingData getById(Long id) {
        return weighingDataMapperMapper.getById(id);
    }

    @Override
    public int insert(WeighingData weighingData) {
        return weighingDataMapperMapper.insert(weighingData);
    }

    @Override
    public WeighingData setIsSend(Long id, String isSend) {
        weighingDataMapperMapper.setIsSend(id, isSend);
        return weighingDataMapperMapper.getById(id);
    }

    @Override
    public List<WeighingData> getUnpushed() {
        return weighingDataMapperMapper.getUnpushed();
    }

    @Override
    public List<WeighingData> queryByOrderNo(List<String> orderNoList) {
        return weighingDataMapperMapper.queryByOrderNo(orderNoList);
    }

    @Override
    public int updateBySuccessful(List<Long> idList) {
        return weighingDataMapperMapper.updateBySuccessful(idList);
    }

    @Override
    public int batchUpdate(List<Long> idList) {
        return weighingDataMapperMapper.batchUpdate(idList);
    }

    @Override
    public List<WeighingData> queryByLastId(String id) {
        return weighingDataMapperMapper.queryByLastId(id);
    }

    @Override
    public WeighingData getLastData() {
        return weighingDataMapperMapper.getLastData();
    }

    @Override
    public List<WeighingData> queryByIdList(List<String> idList) {
        return weighingDataMapperMapper.queryByIdList(idList);
    }
}
