package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ScaleData implements Serializable {

    private static final long serialVersionUId = 1L;

    //这个id从SumData获得。不唯一
    private String id;

    //日期
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd")
    private Date date;

    //时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="HH:mm:ss")
    private Date time;

    //班次
    private String team;

    //自动
    private Boolean auto;

    //斗数
    private BigDecimal number;

    //毛重
    private BigDecimal roughWeight;

    //皮重
    private BigDecimal tareWeight;

    //净重
    private BigDecimal suttleWeight;

    //总重量
    private BigDecimal totalWeight;

    //最后一秤
    private Boolean lastBalance;

    //秤号
    private Integer balance;

    //超差
    private String difference;

    //操作者
    private String handlers;

    //装货员
    private String cargoClerk;

    //数据来源
    private String datasource;

    //同步
    private Boolean sync;

    //REMS0
    private String rems0;

    //REMS1
    private String rems1;

    //REMN0
    private Integer remn0;

    //REMN1
    private Integer remn1;

    //REMN2
    private Integer remn2;

    //REMN3
    private Integer remn3;
}
