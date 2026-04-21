package com.example.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQTTServer {

    private MqttClient publishClient;
    private MqttClient subsribeClient;
    private MqttTopic topic;
    private MqttMessage message;

    @Autowired
    private MqttConnect mqttConnect;
    @Autowired
    private MQTTConfig config;

    public MqttClient publishConnect() {
        try {
            if (publishClient == null) {
                publishClient = new MqttClient(config.getHost(), config.getClientId(), new MemoryPersistence());
            }
            MqttConnectOptions options = mqttConnect.getOptions();
            if (!publishClient.isConnected()) {
                publishClient.connect(options);
                log.info("连接成功");
            } else {
                publishClient.disconnect();
                publishClient.connect(options);
                log.info("重新连接成功");
            }
        } catch (MqttException e) {
            log.error("连接失败", e);
        }
        return publishClient;
    }

    public void sendMQTTMessage(String topic, String data, int qos) {
        try {
            this.publishClient = publishConnect();
            this.topic = this.publishClient.getTopic(topic);
            message = new MqttMessage();
            message.setQos(qos);
            message.setRetained(false);
            message.setPayload(data.getBytes());
            publish(this.topic, message);
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }
    }

    public boolean publish(MqttTopic topic, MqttMessage message) {
        try {
            MqttDeliveryToken token = topic.publish(message);
            token.waitForCompletion();
            boolean complete = token.isComplete();
            log.info("发布消息状态：{}", complete);
            return complete;
        } catch (MqttException e) {
            log.error("发布消息失败", e);
            return false;
        }
    }
}
