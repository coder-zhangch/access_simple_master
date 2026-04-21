package com.example.service;

import com.example.entity.WeighingData;

import java.util.List;

public interface WeighingDataService {

    WeighingData getById(Long id);

    int insert(WeighingData weighingData);

    WeighingData setIsSend(Long id, String isSend);

    //获取未推送的，且lastWeigh=0的数据
    List<WeighingData> getUnpushed();

    //通过 订单编号 列表，查询同一批次的所有称重记录
    List<WeighingData> queryByOrderNo(List<String> orderNoList);

    //将推送成功的数据的 isSend 改为 1已发送
    int updateBySuccessful(List<Long> idList);

    int batchUpdate(List<Long> idList);

    //查询比id更大的数据列表
    List<WeighingData> queryByLastId(String id);

    //获取id最大的一条数据
    WeighingData getLastData();

    //通过id列表查询数据列表
    List<WeighingData> queryByIdList(List<String> idList);
}
