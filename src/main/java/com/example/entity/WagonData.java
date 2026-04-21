package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class WagonData implements Serializable {

    //唯一标识
    private Long id;

    //计量记录标识ID
    private String matchid;

    //车号
    private String carno;

    //物资名称
    private String materialname;

    //物资规格
    private String materialspec;

    //来源单位
    private String sourcename;

    //去向单位
    private String targetname;

    //毛重重量 KG
    private BigDecimal gross;

    //毛重时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    private Date grosstime;

    //皮重重量 KG
    private BigDecimal tare;

    //皮重时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    private Date taretime;

    //净重重量 KG
    private BigDecimal suttle;

    //船名称
    private String ship;

    //记录初创建时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdate;

    //毛重计量员
    private String grossoperator;

    //皮重计量员
    private String tareoperator;

    //毛重衡器名称
    private String grossweigh;

    //皮重衡器名称
    private String tareweigh;

    private String clientId;
}
