package com.example.entity;

import lombok.Data;

import java.util.List;

/**
 * 接收推送过来的地磅数据的类
 */
@Data
public class WagonDataDTO extends WagonData {

    private List<PicData> listPic;

//    private List<VideoData> listVideo;
}
