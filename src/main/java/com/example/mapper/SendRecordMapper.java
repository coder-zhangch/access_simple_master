package com.example.mapper;

import com.example.entity.SendRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SendRecordMapper {

    int insert(@Param("record") SendRecord record);
}
