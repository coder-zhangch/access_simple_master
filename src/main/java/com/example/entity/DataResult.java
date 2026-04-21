package com.example.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataResult implements Serializable {

    private static final long serialVersionUId = 1L;

    private String DataType;
    //该条记录的年 月
    private String YearMonth;
    //该条记录的年 月 日
    private String YearMonthDay;
    //该条记录的年 月 日 时 分 秒
    private String DateTime;
    //该条记录的仓库名
    private String WareHouseName;
    //该条记录的所有温度数据
    private String TempData;
    //全仓最高温（10进制）
    private Integer MaxNum;
    //全仓最低温（10进制）
    private Integer MinNum;
    //全仓平均温（10进制）
    private Integer AverNum;
    //外湿湿度数据（16进制）
    private String outhumiData;
    //内湿湿度数据（16进制）
    private String inhumiData;
    //外湿温度数据（16进制）
    private String outtempData;
    //内湿温度数据（16进制）
    private String intempData;
    //外湿湿度数据（10进制）
    private Integer Out_humiNum;
    //内湿湿度数据（10进制）
    private Integer In_humiNum;
    private Integer Higher_Num;
    private Integer Lower_Num;
    private Integer SFDR;
}
