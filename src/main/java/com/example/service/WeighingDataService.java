package com.example.service;

import com.example.entity.WeighingData;

import java.util.List;

public interface WeighingDataService {

    WeighingData getById(String id);

    //查询比id更大的数据列表
    List<WeighingData> queryByLastId(String id);

    //获取id最大的一条数据
    WeighingData getLastData();

    //通过id列表查询数据列表
    List<WeighingData> queryByIdList(List<String> idList);

    //通过id列表查询数据列表
    List<WeighingData> queryLastFive(String lastId);
}
