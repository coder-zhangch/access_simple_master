package com.example.mapper;

import com.example.entity.GetRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GetRecordMapper {

    int insert(@Param("record") GetRecord record);
}
