package com.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LqData implements Serializable {

    private static final long serialVersionUId = 1L;

    private Long id;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date jcsj;

    private String cfbh;
    private String cfmc;
    private String cfww;
    private String cfws;
    private String cfnw;
    private String cfns;
    private String lspjwd;
    private String lszdw;
    private String lszgw;
    private String lswdzjh;
    private String lssdzjh;
}
