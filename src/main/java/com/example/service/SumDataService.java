package com.example.service;

import com.example.entity.SumData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SumDataService {

    SumData getById(String id);

    //查询最后一条有效的数据
    SumData getLastData(String scaleNo);

    //查询指定id之后的数据
    List<SumData> queryByLastId(String scaleNo, String lastId);

    //查询id列表对应的数据
    List<SumData> queryByIdList(List<String> idList);

    //查询 该秤号 下的最后一条数据
    SumData queryLastByScaleNo(String scaleNo);
}
