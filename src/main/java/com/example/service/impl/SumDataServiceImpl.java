package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.SumData;
import com.example.mapper.SumDataMapper;
import com.example.service.SumDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SumDataServiceImpl implements SumDataService {

    @Autowired
    private SumDataMapper sumDataMapper;

    @Override
    public SumData getById(String id) {
        return sumDataMapper.getById(id);
    }

    @Override
    public SumData getLastData(String scaleNo) {
        //查出id最大的一条数据，再查询比这个id小的，包含 中粮贸易 的id最大的一条数据 返回
        SumData lastData = sumDataMapper.getLastData(scaleNo);
        if(lastData == null){
            return null;
        }

        SumData lastButOne = sumDataMapper.getLastByLastId(scaleNo, lastData.getId());
        return lastButOne;
    }

    @Override
    public List<SumData> queryByLastId(String scaleNo, String lastId) {
        if(StrUtil.isBlank(lastId)){
            return null;
        }
        return sumDataMapper.queryByLastId(scaleNo, lastId);
    }

    @Override
    public List<SumData> queryByIdList(List<String> idList) {
        if(CollUtil.isEmpty(idList)){
            return null;
        }
        return sumDataMapper.queryByIdList(idList);
    }

    @Override
    public SumData queryLastByScaleNo(String scaleNo) {
        return sumDataMapper.queryLastByScaleNo(scaleNo);
    }
}
