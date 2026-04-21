package com.example.service;

import com.example.entity.LqData;
import com.example.entity.WeighingData;

import java.util.List;

public interface LqDataService {

    //根据最后一个id，和粮仓，查询该粮仓在这个id后的所有记录
    LqData getLastBy(String cfbh);
    LqData getLastByDate(String cfbh, String start, String end);

    //查询该粮仓最后一条记录
    LqData getLastByCfbh(String cfbh);

    void dataPush(LqData lqData);
}
