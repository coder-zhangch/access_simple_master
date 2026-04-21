package com.example.entity;

import lombok.Data;

/**
 * 将 地磅 数据推送到大数据的类
 */
@Data
public class WagonDataVO extends WagonData {

    private String listPic;

    private String listVideo;

    private Integer isSend;
}
