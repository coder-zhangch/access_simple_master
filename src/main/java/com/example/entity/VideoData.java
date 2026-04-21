package com.example.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class VideoData implements Serializable {

    private static final long serialVersionUId = 1L;

    //首磅视频流
    private String firVideo;
    //次磅视频流
    private String secVideo;
}
