package com.example.mapper;

import com.example.entity.ScaleData;
import com.example.entity.SumData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SumDataMapper {

    SumData getById(@Param("id") String id);

    //查询最后一条数据
    SumData getLastData(@Param("scaleNo")String scaleNo);

    //查询在最大的id之下的包含 中粮贸易 和 符合 该秤号 的最后一条数据
    SumData getLastByLastId(@Param("scaleNo")String scaleNo, @Param("lastId")String lastId);

    //查询指定id之后的数据
    List<SumData> queryByLastId(@Param("scaleNo")String scaleNo, @Param("lastId")String lastId);

    //查询id列表对应的数据
    List<SumData> queryByIdList(@Param("idList")List<String> idList);

    //查询 该秤号 下的最后一条数据
    SumData queryLastByScaleNo(@Param("scaleNo") String scaleNo);
}
