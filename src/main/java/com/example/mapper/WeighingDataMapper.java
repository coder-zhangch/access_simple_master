package com.example.mapper;

import com.example.entity.WeighingData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WeighingDataMapper {

    WeighingData getById(@Param("id") Long id);

    int insert(@Param("data")WeighingData weighingData);

    List<WeighingData> getUnpushed();

    List<WeighingData> queryByOrderNo(@Param("list") List<String> orderNoList);

    int updateBySuccessful(@Param("idList") List<Long> idList);

    int batchUpdate(@Param("idList") List<Long> idList);

    int setIsSend(@Param("id") Long id, @Param("isSend") String isSend);

    List<WeighingData> queryByLastId(@Param("id") String id);

    WeighingData getLastData();

    List<WeighingData> queryByIdList(@Param("idList") List<String> idList);
}
