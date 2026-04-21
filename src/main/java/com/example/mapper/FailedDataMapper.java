package com.example.mapper;

import com.example.entity.FailedData;
import com.example.entity.LatestData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FailedDataMapper {

    List<FailedData> findByTime(@Param("time") String time);

    Boolean delete();

    Boolean deleteNotIn(@Param("list") List<String> idList);

    Boolean insert(@Param("list") List<FailedData> list);
}
