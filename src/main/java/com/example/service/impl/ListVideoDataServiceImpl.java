package com.example.service.impl;

import com.example.entity.ListVideoDataVO;
import com.example.mapper.ListVideoDataMapper;
import com.example.service.ListVideoDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ListVideoDataServiceImpl implements ListVideoDataService {

    @Autowired
    private ListVideoDataMapper listVideoDataMapper;

    @Override
    public ListVideoDataVO getById(Long id) {
        return listVideoDataMapper.getById(id);
    }

    @Override
    public int insert(ListVideoDataVO vo) {
        return listVideoDataMapper.insert(vo);
    }

    @Override
    public List<ListVideoDataVO> getByIdList(List<Long> idList) {
        return listVideoDataMapper.getByIdList(idList);
    }
}
