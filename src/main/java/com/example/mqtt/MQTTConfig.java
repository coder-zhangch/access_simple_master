package com.example.mqtt;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class MQTTConfig {

    public static final String PREFIX = "publish.mqtt";

    @Value("${publish.mqtt.host}")
    private String host;
    @Value("${publish.mqtt.client-id}")
    private String clientId;
//    @Value("${publish.mqtt.username}")
    private String username;
//    @Value("${publish.mqtt.password}")
    private String password;
    @Value("${publish.mqtt.clean-session}")
    private boolean cleanSession;
    @Value("${publish.mqtt.default-topic}")
    private String defaultTopic;
    @Value("${publish.mqtt.timeout}")
    private int timeout;
    @Value("${publish.mqtt.keepalive}")
    private int keepalive;
    @Value("${publish.mqtt.connection-timeout}")
    private int connectionTimeout;

    public boolean getCleanSession() {
    	return cleanSession;
    }
}
