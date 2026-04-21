package com.example.mqtt;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MqttConnect {

    @Autowired
    private MQTTConfig config;

    public MqttConnectOptions getOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(config.getCleanSession());
        options.setUserName(config.getUsername());
        options.setPassword(config.getPassword() == null ? null : config.getPassword().toCharArray());
        options.setConnectionTimeout(config.getConnectionTimeout());
        options.setKeepAliveInterval(config.getKeepalive());
        return options;
    }
}
