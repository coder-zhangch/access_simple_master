package com.example.mapper;

import com.example.entity.ListVideoDataVO;
import com.example.entity.WagonDataVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ListVideoDataMapper {

    ListVideoDataVO getById(@Param("id") Long id);

    int insert(@Param("data") ListVideoDataVO data);

    List<ListVideoDataVO> getByIdList(@Param("idList") List<Long> idList);
}
