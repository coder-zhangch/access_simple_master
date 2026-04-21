package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.TempDataFormat;
import com.example.common.mqtt.MQTTConfig;
import com.example.common.mqtt.MQTTMessage;
import com.example.common.mqtt.MQTTServer;
import com.example.entity.DataResult;
import com.example.entity.WareHouseInfo;
import com.example.mapper.DataResultMapper;
import com.example.mapper.WareHouseInfoMapper;
import com.example.service.DataResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DataResultServiceImpl implements DataResultService {

    private final static Map<String, String> wareMap = new HashMap<>();
    private final static Map<String, Integer> typeMap = new HashMap<>();

    static {
        wareMap.put("1-1", "8f7891ba3uip79c01");
        wareMap.put("1-5", "8f7891ba3uip79c02");
        wareMap.put("2-1", "8f7891ba3uip79c03");
        wareMap.put("2-3", "8f7891ba3uip79c04");
        wareMap.put("2-4", "8f7891ba3uip79c05");
        wareMap.put("3-1", "8f7891ba3uip79c06");
        wareMap.put("3-3", "8f7891ba3uip79c07");
        wareMap.put("5-3", "8f7891ba3uip79c08");
        wareMap.put("5-4", "8f7891ba3uip79c09");
        wareMap.put("6-1", "8f7891ba3uip79c10");
        wareMap.put("Q31", "8f7891ba3uip79c11");
        wareMap.put("Q32", "8f7891ba3uip79c12");

        //type=1 ： 1-1、1-5、2-1、2-3、2-4、3-1、3-3 一共两圈， 第一圈1；第二圈2-5   第一根15个点，后面13个点
        //type=2 ： Q31、Q32  2圈， 第一圈1-2；第二圈3-11  =19
        //type=3 ： 5-3、5-4、6-1  3圈，第一圈1-3，第二圈4-9，第三圈10-17   =20，18
        typeMap.put("1-1", 1);
        typeMap.put("1-5", 1);
        typeMap.put("2-1", 1);
        typeMap.put("2-3", 1);
        typeMap.put("2-4", 1);
        typeMap.put("3-1", 1);
        typeMap.put("3-3", 1);
        typeMap.put("5-3", 3);
        typeMap.put("5-4", 3);
        typeMap.put("6-1", 3);
        typeMap.put("Q31", 2);
        typeMap.put("Q32", 2);
    }

    @Autowired
    private DataResultMapper dataResultMapper;
    @Autowired
    private WareHouseInfoMapper wareHouseInfoMapper;

    @Autowired
    private MQTTConfig mqttConfig;
    @Autowired
    private MQTTServer mqttserver;

    @Override
    public void sendMessage() {
        List<String> wareNameList = ListUtil
                .toList("1-1", "1-5", "2-1", "2-3", "2-4", "3-1", "3-3", "5-3", "5-4", "6-1", "Q31", "Q32");
//                .toList("1-1");
        List<WareHouseInfo> wareHouseInfoList = wareHouseInfoMapper.getByNameList(wareNameList);
        if(CollUtil.isEmpty(wareHouseInfoList)){
            log.info("未查询到库位数据！");
            return;
        }
        List<DataResult> dataResultList = dataResultMapper.getFinalByWareNameList(wareNameList);
        if(CollUtil.isEmpty(dataResultList)){
            log.info("未查询到历史记录数据！");
            return;
        }
        Map<String, WareHouseInfo> wareHouseInfoMap = wareHouseInfoList.stream()
                .collect(Collectors.toMap(WareHouseInfo::getWareHouseName, Function.identity(), (v1, v2) -> v1));
        List<MQTTMessage> messageList = new ArrayList<>();
        for (DataResult data : dataResultList) {
            try {
                WareHouseInfo wareHouseInfo = wareHouseInfoMap.get(data.getWareHouseName());
                if(wareHouseInfo == null){
                    continue;
                }
                List<Integer> circleConfig = parseLogicRoute(wareHouseInfo.getLogicRoute());
                if(circleConfig == null || circleConfig.size() != wareHouseInfo.getLogicRouteNum()){
                    continue;
                }
//                log.info("==========TempData======{}=====", data.getTempData());
                //查询改数据的TempData是否不合法，不合法则重新查询一次
                if(data.getTempData().indexOf("FFF") == 0){
                    data = dataResultMapper.getFinalByWareName(data.getWareHouseName());
                }

                String siloFormat = TempDataFormat.getSiloFormat(circleConfig, data.getTempData(), typeMap.get(data.getWareHouseName()), data.getAverNum());
                String message = getMessage(data, siloFormat);
                log.info("发送数据：{}", message);
                messageList.add(
                        MQTTMessage.builder()
                                .topic(mqttConfig.getDefaultTopic() + data.getWareHouseName())
                                .message(message)
                                .qos(2)
                                .build()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!messageList.isEmpty()) {
            mqttserver.sendMQTTMessageList(messageList);
        }
    }

    @Override
    public void sendByQos(Integer qos) {
        List<String> wareNameList = ListUtil
                .toList("1-1", "1-5", "2-1", "2-3", "2-4", "3-1", "3-3", "5-3", "5-4", "6-1", "Q31", "Q32");
//                .toList("1-1");
        List<WareHouseInfo> wareHouseInfoList = wareHouseInfoMapper.getByNameList(wareNameList);
        if(CollUtil.isEmpty(wareHouseInfoList)){
            log.info("未查询到库位数据！");
            return;
        }
        List<DataResult> dataResultList = dataResultMapper.getFinalByWareNameList(wareNameList);
        if(CollUtil.isEmpty(dataResultList)){
            log.info("未查询到历史记录数据！");
            return;
        }
        Map<String, WareHouseInfo> wareHouseInfoMap = wareHouseInfoList.stream()
                .collect(Collectors.toMap(WareHouseInfo::getWareHouseName, Function.identity(), (v1, v2) -> v1));
        List<MQTTMessage> messageList = new ArrayList<>();
        for (DataResult data : dataResultList) {
            try {
                WareHouseInfo wareHouseInfo = wareHouseInfoMap.get(data.getWareHouseName());
                if(wareHouseInfo == null){
                    continue;
                }
                List<Integer> circleConfig = parseLogicRoute(wareHouseInfo.getLogicRoute());
                if(circleConfig == null || circleConfig.size() != wareHouseInfo.getLogicRouteNum()){
                    continue;
                }

                String siloFormat = TempDataFormat.getSiloFormat(circleConfig, data.getTempData(), typeMap.get(data.getWareHouseName()), data.getAverNum());
                String message = getMessage(data, siloFormat);
                messageList.add(
                        MQTTMessage.builder()
                                .topic(mqttConfig.getDefaultTopic() + data.getWareHouseName())
                                .message(message)
                                .qos(qos)
                                .build()
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!messageList.isEmpty()) {
            mqttserver.sendMQTTMessageList(messageList);
        }
    }

    @Override
    public void sendByWare(String ware) {
        WareHouseInfo wareHouseInfo = wareHouseInfoMapper.getByName(ware);
        if(wareHouseInfo == null){
            log.info("未查询到库位数据！");
            return;
        }
        DataResult dataResult = dataResultMapper.getFinalByWareName(ware);
        if(dataResult == null){
            log.info("未查询到历史记录数据！");
            return;
        }
        List<Integer> circleConfig = parseLogicRoute(wareHouseInfo.getLogicRoute());
        if(circleConfig == null || circleConfig.size() != wareHouseInfo.getLogicRouteNum()){
            log.info("仓库信息有误！");
        }

        String siloFormat = TempDataFormat.getSiloFormat(circleConfig, dataResult.getTempData(), typeMap.get(ware), dataResult.getAverNum());
        String message = getMessage(dataResult, siloFormat);
        MQTTMessage mqttMessage = MQTTMessage.builder()
                .topic(mqttConfig.getDefaultTopic() + dataResult.getWareHouseName())
                .message(message)
                .qos(2)
                .build();
        mqttserver.sendMQTTMessage(mqttMessage);
    }

    private List<Integer> parseLogicRoute(String logicRoute){
        try {
            List<Integer> list = new ArrayList<>();
            int len = logicRoute.length();

            // 检查字符串长度是否为3的倍数，如果不是可以抛出异常或处理
            if (len % 3 != 0) {
                throw new IllegalArgumentException("字符串长度必须是3的倍数");
            }

            // 每三个字符切分
            for (int i = 0; i < len; i += 3) {
                String substr = logicRoute.substring(i, i + 3);
                list.add(Integer.parseInt(substr));
            }
            return list;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getMessage(DataResult dataResult, String lswdzjh){
        String deviceId = wareMap.get(dataResult.getWareHouseName());
        String jcsj = DateUtil.formatDateTime(new Date());//监测时间
        Integer cfww = StrUtil.isBlank(dataResult.getOuttempData()) ? null : Integer.parseInt(dataResult.getOuttempData(), 16);//仓房外温
        Integer cfws = StrUtil.isBlank(dataResult.getOuthumiData()) ? null : Integer.parseInt(dataResult.getOuthumiData(), 16);//仓房外湿
        Integer humidity = StrUtil.isBlank(dataResult.getInhumiData()) ? null : Integer.parseInt(dataResult.getInhumiData(), 16);//仓房内湿
        Integer temp = StrUtil.isBlank(dataResult.getIntempData()) ? null : Integer.parseInt(dataResult.getIntempData(), 16);//仓房内温
        Integer lszgw = dataResult.getMaxNum();//仓房最高温
        Integer lszdw = dataResult.getMinNum();//仓房最低温
        Integer lspjw = dataResult.getAverNum();//仓房平均温
        String message = "{\n" +
                "  \"deviceId\": \""+deviceId+"\",\n" +
                "  \"jcsj\": \"" + jcsj + "\",\n" +
                "  \"cfww\": \""+ BigDecimal.valueOf(cfww, 1) +"\",\n" +
                "  \"cfws\": \""+BigDecimal.valueOf(cfws, 1)+"\",\n" +
                "  \"humidity\": \""+BigDecimal.valueOf(humidity, 1)+"\",\n" +
                "  \"temp\": \""+BigDecimal.valueOf(temp, 1)+"\",\n" +
                "  \"lszgw\": \""+BigDecimal.valueOf(lszgw, 1)+"\",\n" +
                "  \"lszdw\": \""+BigDecimal.valueOf(lszdw, 1)+"\",\n" +
                "  \"lspjw\": \""+BigDecimal.valueOf(lspjw, 1)+"\",\n" +
                "  \"lswdzjh\": \""+lswdzjh+"\",\n" +
                "  \"lssdzjh\": \"\"\n" +
                "}";
        return message;
    }

    @Override
    public void sendTestMessage() {
        String deviceId = wareMap.get("1-1");
        String jcsj = DateUtil.formatDateTime(new Date());//监测时间
        Integer cfww = 34;//仓房外温
        Integer cfws = 64;//仓房外湿
        Integer humidity = 84;//仓房内湿
        Integer temp = 33;//仓房内温
        Integer lszgw = 43;//仓房最高温
        Integer lszdw = 43;//仓房最低温
        Integer lspjw = 34;//仓房平均温
        String lswdzjh = "32.6,0,0,0|32.9,0,0,1|38.9,0,0,2|43.0,0,0,3|39.1,0,0,4|35.6,0,0,5|35.7,0,0,6|35.3,0,0,7|35.3,0,0,8|34.8,0,0,9|33.4,0,0,10|33.1,0,0,11|33.8,0,0,12|34.6,0,0,13|36.4,0,0,14|\n" +
                "-200,1,0,0|-200,1,0,1|32.9,1,0,2|33.0,1,0,3|37.9,1,0,4|37.1,1,0,5|34.4,1,0,6|34.7,1,0,7|35.1,1,0,8|35.2,1,0,9|34.7,1,0,10|33.5,1,0,11|33.4,1,0,12|34.1,1,0,13|34.7,1,0,14|\n" +
                "-200,1,1,0|-200,1,1,1|32.9,1,1,2|32.6,1,1,3|38.1,1,1,4|37.3,1,1,5|35.4,1,1,6|34.4,1,1,7|34.6,1,1,8|34.7,1,1,9|34.1,1,1,10|33.6,1,1,11|32.9,1,1,12|33.5,1,1,13|34.4,1,1,14|\n" +
                "-200,1,2,0|-200,1,2,1|32.8,1,2,2|32.4,1,2,3|37.4,1,2,4|36.5,1,2,5|34.2,1,2,6|34.2,1,2,7|34.0,1,2,8|34.4,1,2,9|34.6,1,2,10|33.7,1,2,11|33.2,1,2,12|33.3,1,2,13|34.4,1,2,14|\n" +
                "-200,1,3,0|-200,1,3,1|-0.1,1,3,2|-0.1,1,3,3|-0.1,1,3,4|-0.1,1,3,5|-0.1,1,3,6|-0.1,1,3,7|-0.1,1,3,8|-0.1,1,3,9|-0.1,1,3,10|-0.1,1,3,11|-0.1,1,3,12|-0.1,1,3,13|-0.1,1,3,14";
        String message = "{\n" +
                "  \"deviceId\": \""+deviceId+"\",\n" +
                "  \"jcsj\": \"" + jcsj + "\",\n" +
                "  \"cfww\": \""+cfww+"\",\n" +
                "  \"cfws\": \""+cfws+"\",\n" +
                "  \"humidity\": \""+humidity+"\",\n" +
                "  \"temp\": \""+temp+"\",\n" +
                "  \"lszgw\": \""+lszgw+"\",\n" +
                "  \"lszdw\": \""+lszdw+"\",\n" +
                "  \"lspjw\": \""+lspjw+"\",\n" +
                "  \"lswdzjh\": \""+lswdzjh+"\",\n" +
                "  \"lssdzjh\": \"\"\n" +
                "}";
        MQTTMessage mqttMessage = MQTTMessage.builder()
                .topic("zl/th/gfk/1-1")
                .message(message)
                .qos(2)
                .build();
        mqttserver.sendMQTTMessage(mqttMessage);
    }

    public static void main(String[] args) {
        System.out.println(IdUtil.simpleUUID());
        //9169bf3c4fcb4069910e57761da54785
        //9169bf3c4fcb4069910Token20251107
    }

    private final static String APP_ID = "wxe2a20a45f15a6cca";
    private final static String APP_SECRET = "8525ea6d60d4e9aff67204be4d724a1d";
    @Override
    public void sendToOfficialAccounts() {
        String openId = "test";
        String accessToken = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken;

        Map<String, Object> message = new HashMap<>();
        message.put("touser", openId);
        message.put("msgType", "text");
        message.put("text", Collections.singletonMap("content", "Hello World!"));

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(message, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
        Map<String, Object> resBody = response.getBody();
        if(resBody != null && "0".equals(resBody.get("errcode").toString())){
            System.out.println("消息发送成功！");
        }else {
            System.out.println("消息发送失败：" + resBody);
        }
    }

    private static String accessToken;
    private static long tokenExpiresAt;

    //获取access_token（带缓存）
    private synchronized String getAccessToken(){
        if(accessToken == null || System.currentTimeMillis() >= tokenExpiresAt){
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APP_ID + "&secret=" + APP_SECRET;
            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> result = restTemplate.getForObject(url, Map.class);
            if(result != null && result.containsKey("access_token")){
                accessToken = (String) result.get("access_token");
                int expires = (int) result.get("expires_in");
                tokenExpiresAt = System.currentTimeMillis() + (expires - 300) * 1000L;//提前5分钟过期
            }else {
                throw new RuntimeException("获取 access_token 失败：" + result);
            }
        }
        return accessToken;
    }

    @Override
    public String getDeviceQrCode(String deviceId) {
        //生成ticket
        String ticket = createPermanentQrCode("DEVICE_" + deviceId);
        //获取二维码图片链接
        String imageUrl = getQrCodeImageUrl(ticket);
        return imageUrl;
    }

    private String createPermanentQrCode(String sceneStr){
        String accessToken = getAccessToken();
        String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken;

        //构造请求体
        Map<String, Object> body = new HashMap<>();
        body.put("action_name", "QR_LIMIT_STR_SCENE");//字符串型二维码
        Map<String, Object> scene = new HashMap<>();
        scene.put("scene_str", sceneStr); // 如 "DEVICE_POWER_001"
        body.put("action_info", Collections.singletonMap("scene", scene));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.postForObject(url, entity, Map.class);

        if (response == null || response.containsKey("errcode")) {
            throw new RuntimeException("创建二维码失败: " + response);
        }

        // 返回 ticket，用于生成二维码图片 URL
        return (String) response.get("ticket");
    }

    public String getQrCodeImageUrl(String ticket) {
        try {
            String encodedTicket = URLEncoder.encode(ticket, "UTF-8");
            return "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + encodedTicket;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
