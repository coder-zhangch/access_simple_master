package com.example.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ListVideoData implements Serializable {

    //唯一标识
    private Long id;

    //计量记录标识ID
    private String matchid;

    //车号
    private String carno;
}
