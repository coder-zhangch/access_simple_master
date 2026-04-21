package com.example.service;

import com.example.entity.FailedData;
import com.example.entity.LatestData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FailedDataService {

    /**
     * 找到大于这个时间的所有数据
     * @param time
     * @return
     */
    List<FailedData> findByTime(@Param("time") String time);

    /**
     * 删除全部数据
     * @return
     */
    Boolean delete();

    /**
     * 删除 除idList 外的全部数据
     * @return
     */
    Boolean deleteNotIn(List<String> idList);

    /**
     * 添加数据列表
     * @param list
     * @return
     */
    Boolean insert(@Param("list") List<FailedData> list);
}
