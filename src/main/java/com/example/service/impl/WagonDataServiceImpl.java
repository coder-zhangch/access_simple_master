package com.example.service.impl;

import com.example.entity.WagonDataDTO;
import com.example.entity.WagonDataVO;
import com.example.mapper.WagonDataMapper;
import com.example.request.BaseResult;
import com.example.service.WagonDataService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WagonDataServiceImpl implements WagonDataService {

    @Autowired
    private WagonDataMapper wagonDataMapper;

    @Override
    public WagonDataVO getById(Long id) {
        return wagonDataMapper.getById(id);
    }

    @Override
    public int insert(WagonDataVO vo) {
        return wagonDataMapper.insert(vo);
    }

    @Override
    public List<WagonDataVO> getUnpushed() {
        return wagonDataMapper.getUnpushed();
    }

    @Override
    public int updateBySuccessful(List<Long> idList) {
        return wagonDataMapper.updateBySuccessful(idList);
    }
}
