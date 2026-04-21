package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class WeighingData implements Serializable {

    private static final long serialVersionUId = 1L;

    //唯一标识
    private Long EntryID;

    private Long id;
    //单位编码(2021111.推送数据时，报文中需要包含这个信息)
    private String clientId;
    //仓储编号(仓位号：实际入粮仓/出粮仓)
    private String CCNo;
    //承运单位(船舶代理)
    private String cydw;
    //单位名称(委托单位)即“收货单位”
    private String dwmc;
    //运输工具(船号)
    private String ysgj;
    //货名
    private String materialName;
    //操作者
    private String operator;
    //秤号
    private String scaleNo;
    //净重 单位:吨
    private BigDecimal suttle;
    //皮重 单位:吨
    private BigDecimal tare;
    //毛重 单位:吨
    private BigDecimal gross;
    //总重 单位:吨
    private BigDecimal totalWeight;
    //日期
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd")
    private Date weightDate;
    //时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    private Date weightTime;
    //装货员
    private String loader;

    //是否自动
    private Integer ziDong;
    //订单编号(提货单)
    private String orderNo;
    //是否最后一秤
    private Integer lastWeigh;
    //数据来源
    private String dataFrom;
    //单价
    private BigDecimal danPrice;
    //金额
    private BigDecimal amount;
    //班次
    private String banCi;

    //是否同步
    private Integer synchroFlag;
    //斗数
    private Integer doushu;
    //是否收货
    private Integer shFlag;
    private String remf0;
    private String remf1;
    private String remf2;
    private String remf3;
    private String remf4;
    private String remf5;
    private String remf6;
    private String remf7;
    private String remf8;
    private String remf9;
    private String remn0;
    private String remn1;
    private String remn2;
    private String remn3;
    private String remn4;
    private String rems0;
    private String rems1;
    private String rems2;
    private String rems3;
    private String rems4;
    private String rems5;
    private String rems6;
    private String rems7;
    private String rems8;
    private String rems9;
    //是否发送，1已发送，0未发送
    private Integer isSend;
}
