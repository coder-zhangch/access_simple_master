package com.example.mapper;

import com.example.entity.LqData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface LqDataMapper {

    LqData getLastBy(@Param("cfbh") String cfbh);

    LqData getLastByDate(@Param("cfbh") String cfbh, @Param("start") String start, @Param("end") String end);

    LqData getLastByCfbh(@Param("cfbh") String cfbh);
}
