package com.example.mapper;

import com.example.entity.LatestData;
import com.example.entity.SumData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LatestDataMapper {

    LatestData findOne();

    Boolean delete();

    Boolean insert(@Param("latestData") LatestData latestData);
}
