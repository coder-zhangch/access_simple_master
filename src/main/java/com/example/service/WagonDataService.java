package com.example.service;

import com.example.entity.WagonDataDTO;
import com.example.entity.WagonDataVO;
import com.example.request.BaseResult;

import java.util.List;

public interface WagonDataService {

    WagonDataVO getById(Long id);

    int insert(WagonDataVO vo);

    List<WagonDataVO> getUnpushed();

    int updateBySuccessful(List<Long> idList);
}
