package com.example.mapper;

import com.example.entity.GetRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface GetRecordMapper {

    int insert(@Param("record") GetRecord record);

    //通过库位和日期查询第一条历史记录并推送
    GetRecord getHistory(@Param("date") String date);
}
