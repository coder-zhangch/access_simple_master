package com.example.mapper;

import com.example.entity.DataResult;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataResultMapper {

    DataResult getFinalByWareName(@Param("name") String name);

    List<DataResult> getFinalByWareNameList(@Param("nameList") List<String> nameList);
}
