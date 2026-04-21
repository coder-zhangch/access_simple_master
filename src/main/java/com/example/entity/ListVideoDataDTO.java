package com.example.entity;

import lombok.Data;

import java.util.List;

@Data
public class ListVideoDataDTO extends ListVideoData {

    private List<VideoData> listVideo;
}
