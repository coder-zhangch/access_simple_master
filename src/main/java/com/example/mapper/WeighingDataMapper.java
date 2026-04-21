package com.example.mapper;

import com.example.entity.WeighingData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WeighingDataMapper {

    WeighingData getById(@Param("EntryID") String id);

    List<WeighingData> queryByLastId(@Param("EntryID") String id);

    WeighingData getLastData();

    List<WeighingData> queryByIdList(@Param("idList") List<String> idList);

    List<WeighingData> queryLastFive(@Param("lastId") String lastId);
}
