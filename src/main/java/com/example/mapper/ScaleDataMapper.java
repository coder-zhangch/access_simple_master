package com.example.mapper;

import com.example.entity.ScaleData;
import com.example.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScaleDataMapper {

    List<ScaleData> getById(@Param("id") String id);
}
