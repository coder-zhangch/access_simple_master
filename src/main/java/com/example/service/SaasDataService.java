package com.example.service;

import com.example.entity.WagonDataVO;

import java.util.List;

public interface SaasDataService {

    int saveGetRecord();

    int saveSendRecord();

    void getAndSend();

    void handleSendRecord(String resultData);
}
