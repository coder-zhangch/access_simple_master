package com.example.request;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.PicData;
import com.example.entity.VideoData;
import com.example.entity.WagonDataVO;
import com.example.entity.WeighingData;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpsRequest {

    //登录请求url
    private final static String loginURL = "http://ctszhlk.cofco.com:8038/grainStore/validate";
    //用户名
    private final static String username = "dss_1112";
    //密码
    private final static String password = "dss@1112";
    //登录公钥
    private final static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW9Sbyd+niEpoOq+JIg9vyjbOS2g+cBMr4lsEbXG0nv7rVFgO9hlZ6PhRD8Hof4ij6Wk2/lyF2wtYpLCBwOBhltJD1H3OBYHEQH3xcImnIDZ9BrEhE2qOiqCxWiW9lyK4Xlq8CfnhHUAk6tI3deom1ZpBGUX6G/9nCHk8TMVADrQIDAQAB";

    //散料 推送数据请求url
    private final static String pushURL = "http://ctszhlk.cofco.com:8038/grainStore/jYPoundData";

    //地磅 推送数据请求url
    private final static String wagonPushURL = "http://ctszhlk.cofco.com:8038/grainStore/poundDataNew";

    private final static String ONE = "1";
    private final static String ZERO = "0";
    private final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static String tokenStr = null;

    private static void first(){
        BaseResult<String> token = getToken();
        if(token == null || token.getToken() == null){
            return;
        }
        tokenStr = token.getToken();
    }

    public static BaseResult<String> getToken(){
        try {
            PublicKey key = RSAUtils.getPublicKey(publicKey);
            String loginUsername = RSAUtils.encryptByPublicKey(username, key);
            String loginPassword = RSAUtils.encryptByPublicKey(password, key);
            //请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("loginId", loginUsername);
            params.put("password", loginPassword);

            String body = HttpUtil.post(loginURL, params);
            log.info("Get Token：{}", body);
            BaseResult<String> result = JSONObject.parseObject(body, BaseResult.class);
            return result;
        } catch (Exception e) {
            log.info("Get Token Failure！");
            return null;
        }
    }

    //散料数据推送
    public static BaseResult<String> pushData(String message, WeighingData weighingData){
        if(weighingData == null){
            return BaseResult.fail();
        }
        try {
            first();
            if(tokenStr == null){
                return BaseResult.fail();
            }
            JSONObject json = new JSONObject();
            json.put("id", weighingData.getId());
            json.put("clientId", "202110115");
            json.put("ccNo", weighingData.getCcNo());
            json.put("cydw", weighingData.getCydw());
            json.put("ysgj", weighingData.getYsgj());
            json.put("materialName", weighingData.getMaterialName());
            json.put("operator", weighingData.getOperator());
            json.put("scaleNo", weighingData.getScaleNo());
            json.put("suttle", weighingData.getSuttle() != null ? weighingData.getSuttle().stripTrailingZeros().toPlainString() : null);
            json.put("tare", weighingData.getTare() != null ? weighingData.getTare().stripTrailingZeros().toPlainString() : null);
            json.put("totalWeight", weighingData.getTotalWeight() != null ? weighingData.getTotalWeight().stripTrailingZeros().toPlainString() : null);
            json.put("gross", weighingData.getGross() != null ? weighingData.getGross().stripTrailingZeros().toPlainString() : null);
            json.put("weightDate", DateUtil.format(weighingData.getWeightDate(), "yyyy-MM-dd"));
            json.put("weightTime", DateUtil.format(weighingData.getWeightTime(), "yyyy-MM-dd HH:mm:ss"));
            json.put("loader", weighingData.getLoader());

            json.put("ziDong", weighingData.getZiDong());
            json.put("orderNo", weighingData.getOrderNo());

            json.put("dwmc", weighingData.getDwmc());
            json.put("lastWeigh", weighingData.getLastWeigh());
            json.put("dataFrom", weighingData.getDataFrom());
            json.put("banCi", weighingData.getBanCi());
            json.put("danPrice", weighingData.getDanPrice());
            json.put("amount", weighingData.getAmount());

            log.info("====={}=====：{}--{}", message, weighingData.getId(), json);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("token", tokenStr);
            headerMap.put("Content-Type", "application/json");
            String body = HttpRequest.post(pushURL)
                    .body(json.toJSONString())
                    .addHeaders(headerMap)
                    .timeout(20000)
                    .execute()
                    .body();
            log.info("====={}Result=====：{}--{}", message, weighingData.getId(), body);
            BaseResult<String> result = JSONObject.parseObject(body, BaseResult.class);
            return result;
        } catch (Exception e) {
            log.info("====={}Error=====", message);
            return BaseResult.fail();
        }
    }

    //地磅数据推送
    public static BaseResult<String> wagonPushData(String message, WagonDataVO wagonData){
        if(wagonData == null){
            return BaseResult.fail();
        }
        try {
            if(tokenStr == null){
                first();
                if(tokenStr == null){
                    return BaseResult.fail();
                }
            }
            JSONObject json = new JSONObject();
            json.put("id", wagonData.getId());
            json.put("matchid", wagonData.getMatchid());
            json.put("carno", wagonData.getCarno());
            json.put("materialname", wagonData.getMaterialname());
            json.put("materialspec", wagonData.getMaterialspec());
            json.put("sourcename", wagonData.getSourcename());
            json.put("targetname", wagonData.getTargetname());
            json.put("gross", wagonData.getGross() != null ? wagonData.getGross().stripTrailingZeros().toPlainString() : null);
            json.put("grosstime", DateUtil.format(wagonData.getGrosstime(), DATETIME_FORMAT));
            json.put("tare", wagonData.getTare() != null ? wagonData.getTare().stripTrailingZeros().toPlainString() : null);
            json.put("taretime", DateUtil.format(wagonData.getGrosstime(), DATETIME_FORMAT));
            json.put("suttle", wagonData.getSuttle() != null ? wagonData.getSuttle().stripTrailingZeros().toPlainString() : null);
            json.put("ship", wagonData.getShip());
            json.put("createdate", DateUtil.format(wagonData.getCreatedate(), "yyyy-MM-dd HH:mm:ss"));
            json.put("grossoperator", wagonData.getGrossoperator());
            json.put("tareoperator", wagonData.getTareoperator());
            json.put("grossweigh", wagonData.getGrossweigh());
            json.put("tareweigh", wagonData.getTareweigh());
            json.put("clientId", "202110115");
            log.info("====={}=====：{}--{}", message, wagonData.getId(), json);
            //json转List
            List<PicData> picList = (List<PicData>) JSONObject.parseObject(wagonData.getListPic(), List.class);
            json.put("listPic", picList);
            List<VideoData> videoList = (List<VideoData>) JSONObject.parseObject(wagonData.getListVideo(), List.class);
            json.put("listVideo", videoList);

            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("token", tokenStr);
            headerMap.put("Content-Type", "application/json");
            String body = HttpRequest.post(wagonPushURL)
                    .body(json.toJSONString())
                    .addHeaders(headerMap)
                    .timeout(20000)
                    .execute()
                    .body();
            log.info("====={}Result=====：{}--{}", message, wagonData.getId(), body);
            BaseResult<String> result = JSONObject.parseObject(body, BaseResult.class);
            return result;
        } catch (Exception e) {
            log.info("====={}Error=====", message);
            return BaseResult.fail();
        }
    }
}
