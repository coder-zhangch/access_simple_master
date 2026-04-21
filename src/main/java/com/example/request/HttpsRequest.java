package com.example.request;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.WeighingData;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpsRequest {

    //登录请求url
    //http://lgort.cofcotrading.com:8038
    //http://ctszhlk.cofco.com:8038
    private final static String loginURL = "http://ctszhlk.cofco.com:8038/grainStore/validate";
    //用户名
    private final static String username = "dss_1112";
    //密码
    private final static String password = "dss@1112";
    //登录公钥
    private final static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW9Sbyd+niEpoOq+JIg9vyjbOS2g+cBMr4lsEbXG0nv7rVFgO9hlZ6PhRD8Hof4ij6Wk2/lyF2wtYpLCBwOBhltJD1H3OBYHEQH3xcImnIDZ9BrEhE2qOiqCxWiW9lyK4Xlq8CfnhHUAk6tI3deom1ZpBGUX6G/9nCHk8TMVADrQIDAQAB";
    //推送数据请求url
    private final static String pushURL = "http://ctszhlk.cofco.com:8038/grainStore/jYPoundData";

    private final static String ONE = "1";
    private final static String ZERO = "0";
    private final static int ZERO_NUM = 0;

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

    public static BaseResult<String> pushData(String message, WeighingData weighingData){
        if(weighingData == null){
            return null;
        }
        try {
            BaseResult<String> token = getToken();
            if(token == null || token.getToken() == null){
                return null;
            }
            JSONObject json = new JSONObject();
            json.put("id", weighingData.getEntryID());
            json.put("clientId", "20211212");
            json.put("ccNo", weighingData.getCCNo() == null ? ONE : weighingData.getCCNo());
            json.put("cydw", weighingData.getCydw() == null ? ONE : weighingData.getCydw());
            json.put("dwmc", weighingData.getDwmc() == null ? ONE : weighingData.getDwmc());
            json.put("ysgj", weighingData.getYsgj() == null ? ONE : weighingData.getYsgj());
            json.put("materialName", weighingData.getMaterialName() == null ? ONE : weighingData.getMaterialName());
            json.put("operator", weighingData.getOperator() == null ? ONE : weighingData.getOperator());
            json.put("scaleNo", weighingData.getScaleNo() == null ? ONE : weighingData.getScaleNo());
            json.put("suttle", weighingData.getSuttle() == null ? ZERO_NUM : weighingData.getSuttle().stripTrailingZeros().toPlainString());
            json.put("tare", weighingData.getTare() == null ? ZERO_NUM : weighingData.getTare().stripTrailingZeros().toPlainString());
            json.put("totalWeight", weighingData.getTotalWeight() == null ? ZERO_NUM : weighingData.getTotalWeight().stripTrailingZeros().toPlainString());
            json.put("gross", weighingData.getGross() == null ? ZERO_NUM : weighingData.getGross().stripTrailingZeros().toPlainString());
            json.put("weightDate", weighingData.getWeightDate()== null ? "1721836800000" :String.valueOf(weighingData.getWeightDate().getTime()));
            json.put("weightTime", weighingData.getWeightTime() == null ? "2024-07-25 00:00:00" : DateUtil.format(weighingData.getWeightTime(), "yyyy-MM-dd HH:mm:ss"));
            json.put("loader", weighingData.getLoader() == null ? ONE : weighingData.getLoader());

            json.put("ziDong", weighingData.getZiDong() == null ? 1 : weighingData.getLoader());
            json.put("orderNo", weighingData.getOrderNo() == null ? ONE : weighingData.getOrderNo());
            json.put("lastWeigh", weighingData.getLastWeigh() == null ? 0 : weighingData.getLastWeigh());
            json.put("dataFrom", weighingData.getDataFrom() == null ? ONE : weighingData.getDataFrom());
            json.put("danPrice", weighingData.getDanPrice() == null ? ZERO_NUM : weighingData.getDanPrice().stripTrailingZeros().toPlainString());
            json.put("amount", weighingData.getAmount() == null ? ZERO_NUM : weighingData.getAmount().stripTrailingZeros().toPlainString());
            json.put("banCi", weighingData.getBanCi() == null ? ONE : weighingData.getBanCi());

            json.put("synchroFlag", weighingData.getSynchroFlag());
            json.put("douShu", weighingData.getDoushu() == null ? ONE : weighingData.getDoushu());
            json.put("shFlag", weighingData.getShFlag());
            json.put("remf0", weighingData.getRemf0());
            json.put("remf1", weighingData.getRemf1());
            json.put("remf2", weighingData.getRemf2());
            json.put("remf3", weighingData.getRemf3());
            json.put("remf4", weighingData.getRemf4());
            json.put("remf5", weighingData.getRemf5());
            json.put("remf6", weighingData.getRemf6());
            json.put("remf7", weighingData.getRemf7());
            json.put("remf8", weighingData.getRemf8());
            json.put("remf9", weighingData.getRemf9());
            json.put("remn0", weighingData.getRemn0());
            json.put("remn1", weighingData.getRemn1());
            json.put("remn2", weighingData.getRemn2());
            json.put("remn3", weighingData.getRemn3());
            json.put("remn4", weighingData.getRemn4());
            json.put("rems0", weighingData.getRems0());
            json.put("rems1", weighingData.getRems1());
            json.put("rems2", weighingData.getRems2());
            json.put("rems3", weighingData.getRems3());
            json.put("rems4", weighingData.getRems4());
            json.put("rems5", weighingData.getRems5());
            json.put("rems6", weighingData.getRems6());
            json.put("rems7", weighingData.getRems7());
            json.put("rems8", weighingData.getRems8());
            json.put("rems9", weighingData.getRems9());

            log.info("====={}=====：{}--{}", message, weighingData.getEntryID(), json);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("token", token.getToken());
            headerMap.put("Content-Type", "application/json");
            String body = HttpRequest.post(pushURL)
                    .body(json.toJSONString())
                    .addHeaders(headerMap)
                    .timeout(20000)
                    .execute()
                    .body();
            log.info("====={}Result=====：{}--{}", message, weighingData.getEntryID(), body);
            BaseResult<String> result = JSONObject.parseObject(body, BaseResult.class);
            return result;
        } catch (Exception e) {
            log.info("====={}Error=====", message);
            throw new RuntimeException(e);
        }
    }
}
