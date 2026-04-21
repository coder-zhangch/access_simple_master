package com.example.common.mqtt;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void sendMQTTMessage(MQTTMessage mqttMessage) {
        try {
            this.publishClient = publishConnect();
            this.topic = this.publishClient.getTopic(mqttMessage.getTopic());
            message = new MqttMessage();
            message.setQos(mqttMessage.getQos());
            message.setRetained(false);
            message.setPayload(mqttMessage.getMessage().getBytes());
            publish(this.topic, message);
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }finally {
            try {
                publishClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public void sendMQTTMessageList(List<MQTTMessage> messageList){
        try {
            this.publishClient = publishConnect();
            for (MQTTMessage msg : messageList) {
                this.topic = this.publishClient.getTopic(msg.getTopic());
                message = new MqttMessage();
                message.setQos(msg.getQos());
                message.setRetained(false);
                message.setPayload(msg.getMessage().getBytes());
                publish(this.topic, message);
            }
        } catch (Exception e) {
            log.error("发送消息失败", e);
        }finally {
            try {
                publishClient.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    public boolean publish(MqttTopic topic, MqttMessage message) {
//        if(true){
//            return false;
//        }
        try {
            MqttDeliveryToken token = topic.publish(message);
            token.waitForCompletion();
            boolean complete = token.isComplete();
            log.info("{}发布消息状态：{}", topic, complete);
            return complete;
        } catch (MqttException e) {
            log.error("{}发布消息失败{}", topic, e);
            return false;
        }
    }
}
