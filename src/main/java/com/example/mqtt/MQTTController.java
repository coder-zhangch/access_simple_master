package com.example.mqtt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mqtt")
public class MQTTController {

    @Autowired
    private MQTTServer mqttserver;

    @GetMapping("test")
    public String test(){
        String message = "{\n" +
                "  \"deviceId\": \"1231231\",\n" +
                "  \"jcsj\": \"2025-01-01 12:12:12\",\n" +
                "  \"cfww\": 22.1,\n" +
                "  \"cfws\": 22.1,\n" +
                "  \"humidity\": 22.1,\n" +
                "  \"temp\": 22.1,\n" +
                "  \"lszgw\": 22.1,\n" +
                "  \"lszdw\": 22.1,\n" +
                "  \"lspjw\": 22.1,\n" +
                "  \"lswdzjh\": \"18.6,0,0,0|17.5,0,0,1|17.7,0,0,2|17.8,0,0,3|17.6,0,0,4|18.0,0,1,0\",\n" +
                "  \"lssdzjh\": \"18.6,0,0,0|17.5,0,0,1|17.7,0,0,2|17.8,0,0,3|17.6,0,0,4|18.0,0,1,0\"\n" +
                "}";
        mqttserver.sendMQTTMessage("/zl/th/gfk/gfk001", message, 1);
        mqttserver.sendMQTTMessage("zl/th/gfk/gfk001", message, 1);
        return "发送完成";
    }
}
