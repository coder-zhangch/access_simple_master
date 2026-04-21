package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 数据库的对象
 */
@Data
public class WeighingData implements Serializable {

    private static final long serialVersionUId = 1L;

    private Long id;
    //单位编码(152001.推送数据时，报文中需要包含这个信息)
    private String clientId;
    //仓储编号(仓位号：实际入粮仓/出粮仓)
    private String ccNo;
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
    //净重
    private BigDecimal suttle;
    //皮重
    private BigDecimal tare;
    //毛重
    private BigDecimal gross;
    //总重
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

    //是否发送，1已发送，0未发送
    private Integer isSend;
}
