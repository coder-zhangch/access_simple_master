package com.example.mapper;

import com.example.entity.WagonDataVO;
import com.example.entity.WeighingData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WagonDataMapper {

    WagonDataVO getById(@Param("id") Long id);

    int insert(@Param("data") WagonDataVO data);

    List<WagonDataVO> getUnpushed();

    int updateBySuccessful(@Param("idList") List<Long> idList);
}
