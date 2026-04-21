package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 推送到这边的数据对象
 */
@Data
public class ScaleData implements Serializable {

    private static final long serialVersionUId = 1L;

    private String clientId;

    private String ccNo;

    private String cydw;

    private Long id;

    private String loader;

    private String materialName;

    private String operator;

    private String orderNo;

    private String scaleNo;

    private BigDecimal gross;

    private BigDecimal suttle;

    private BigDecimal tare;

    private BigDecimal totalWeight;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd")
    private Date weightDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    private Date weightTime;

    private String ysgj;

    private Integer ziDong;

    private String dwmc;

    private Integer lastWeigh;

    private String dataFrom;

    private String banCi;

    private BigDecimal danPrice;

    private BigDecimal amount;

}
