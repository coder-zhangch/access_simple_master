package com.example.service;

import com.example.entity.LatestData;
import com.example.entity.ScaleData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface LatestDataService {

    /**
     * 找到时间最近的第一个数据
     * @return
     */
    LatestData findOne();

    /**
     * 将数据全部删除
     * @return
     */
    Boolean delete();

    /**
     * 添加一个数据
     * @param latestData
     * @return
     */
    Boolean insert(@Param("latestData") LatestData latestData);
}
