package com.example.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PicData implements Serializable {

    private static final long serialVersionUId = 1L;

    //首磅照片流1
    private String firPic1;
    //首磅照片流2
    private String firPic2;
    //首磅照片流3
    private String firPic3;
    //首磅照片流4
    private String firPic4;

    //次磅照片流1
    private String secPic1;
    //次磅照片流2
    private String secPic2;
    //次磅照片流3
    private String secPic3;
    //次磅照片流4
    private String secPic4;
}
