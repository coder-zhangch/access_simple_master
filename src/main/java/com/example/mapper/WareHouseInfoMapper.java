package com.example.mapper;

import com.example.entity.WareHouseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WareHouseInfoMapper {

    WareHouseInfo getByName(@Param("name") String name);

    List<WareHouseInfo> getByNameList(@Param("nameList") List<String> nameList);
}
