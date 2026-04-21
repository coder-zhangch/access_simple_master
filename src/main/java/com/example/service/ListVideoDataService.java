package com.example.service;

import com.example.entity.ListVideoDataVO;
import com.example.entity.WagonDataVO;

import java.util.List;

public interface ListVideoDataService {

    ListVideoDataVO getById(Long id);

    int insert(ListVideoDataVO vo);

    List<ListVideoDataVO> getByIdList(List<Long> idList);
}
