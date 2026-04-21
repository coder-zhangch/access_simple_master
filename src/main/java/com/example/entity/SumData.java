package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SumData implements Serializable {

    private static final long serialVersionUId = 1L;

    //唯一id
    private String id;

    //订单编号
    private String orderNo;

    //单位名称
    private String dwmc;

    //承运单位
    private String cydw;

    //运输工具
    private String ysgj;

    //货物名称
    private String materialName;

    //货物等级
    private String goodsLevel;

    //仓储
    private String ccNo;

    //收货
    private Boolean shFlag;

    //出发地点
    private String depart;

    //到达地点
    private String arrival;

    //目标重量
    private BigDecimal targetWeight;

    //日期
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    private Date weightDate;

    //时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    private Date weightTime;

    //班次
    private String banCi;

    //自动
    private Boolean ziDong;

    //斗数
    private BigDecimal douShu;

    //毛重
    private BigDecimal gross;

    //皮重
    private BigDecimal tare;

    //净重
    private BigDecimal suttle;

    //总重量
    private BigDecimal totalWeight;

    //最后一秤
    private Boolean lastWeigh;

    //秤号
    private BigDecimal scaleNo;

    //超差
    private String difference;

    //操作者
    private String operator;

    //装货员
    private String loader;

    //单价
    private BigDecimal danPrice;

    //金额
    private BigDecimal amount;

    //数据来源
    private String dataFrom;

    //同步
    private Boolean synchroFlag;

    //REMS0
    private String rems0;

    //REMS1
    private String rems1;

    //REMS2
    private String rems2;

    //REMS3
    private String rems3;

    //REMS4
    private String rems4;

    //REMS5
    private String rems5;

    //REMS6
    private String rems6;

    //REMS7
    private String rems7;

    //REMS8
    private String rems8;

    //REMS9
    private String rems9;

    //REMN0
    private BigDecimal remn0;

    //REMN1
    private BigDecimal remn1;

    //REMN2
    private BigDecimal remn2;

    //REMN3
    private BigDecimal remn3;

    //REMN4
    private BigDecimal remn4;

    //REMF0
    private BigDecimal remf0;

    //REMF1
    private BigDecimal remf1;

    //REMF2
    private BigDecimal remf2;

    //REMF3
    private BigDecimal remf3;

    //REMF4
    private BigDecimal remf4;

    //REMF5
    private BigDecimal remf5;

    //REMF6
    private BigDecimal remf6;

    //REMF7
    private BigDecimal remf7;

    //REMF8
    private BigDecimal remf8;

    //REMF9
    private BigDecimal remf9;
}
