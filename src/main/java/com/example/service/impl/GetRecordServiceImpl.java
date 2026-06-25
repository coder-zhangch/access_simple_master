package com.example.service.impl;

import com.example.entity.GetRecord;
import com.example.mapper.GetRecordMapper;
import com.example.service.GetRecordService;
import com.example.service.SaasDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class GetRecordServiceImpl implements GetRecordService {

    @Autowired
    private GetRecordMapper getRecordMapper;
    @Lazy
    @Autowired
    private SaasDataService saasDataService;

    @Override
    @Transactional
    public String sendHistory(String date) {
        GetRecord record = getRecordMapper.getHistory(date+"%");
        if(record == null){
            return "未找到数据记录！";
        }

        saasDataService.handleSendRecord(record.getGetData());
        return "推送历史数据成功！";
    }
}
