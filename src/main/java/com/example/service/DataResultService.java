package com.example.service;

public interface DataResultService {

    void sendMessage();

    void sendByQos(Integer qos);

    void sendByWare(String ware);

    void sendTestMessage();

    //推送消息到微信公众号
    void sendToOfficialAccounts();

    String getDeviceQrCode(String deviceId);
}
