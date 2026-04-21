package com.example.request;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.SumData;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class HttpsRequest {

    //登录请求url
    private final static String loginURL = "http://lgort.cofcotrading.com:8038/grainStore/validate";
    //用户名
    private final static String username = "dss_1112";
    //密码
    private final static String password = "dss@1112";
    //登录公钥
    private final static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW9Sbyd+niEpoOq+JIg9vyjbOS2g+cBMr4lsEbXG0nv7rVFgO9hlZ6PhRD8Hof4ij6Wk2/lyF2wtYpLCBwOBhltJD1H3OBYHEQH3xcImnIDZ9BrEhE2qOiqCxWiW9lyK4Xlq8CfnhHUAk6tI3deom1ZpBGUX6G/9nCHk8TMVADrQIDAQAB";
    //推送数据请求url
    private final static String pushURL = "http://lgort.cofcotrading.com:8038/grainStore/jYPoundData";

    private final static String ONE = "1";
    private final static String ZERO = "0";

    public static BaseResult<String> getToken(){
        try {
            PublicKey key = RSAUtils.getPublicKey(publicKey);
            String loginUsername = RSAUtils.encryptByPublicKey(username, key);
            String loginPassword = RSAUtils.encryptByPublicKey(password, key);
            //请求参数
            Map<String, String> params = new HashMap<>();
            params.put("loginId", loginUsername);
            params.put("password", loginPassword);

            String res = HttpUtils.post(loginURL, new HashMap<>(), params);
            log.info("获取token：{}", res);
            BaseResult<String> result = JSONObject.parseObject(res, BaseResult.class);
            return result;
        } catch (Exception e) {
            log.info("获取token失败！");
            return null;
        }
    }

    public static BaseResult<String> pushData(String message, SumData sumData){
        if(sumData == null){
            return null;
        }
        try {
            BaseResult<String> token = getToken();
            if(token == null || token.getToken() == null){
                return null;
            }
            JSONObject json = new JSONObject();
            json.put("id", sumData.getId());
            json.put("clientId", "2021111");
            json.put("amount", sumData.getAmount() != null ? sumData.getAmount().stripTrailingZeros().toPlainString() : null);
            json.put("banCi", sumData.getBanCi());
            json.put("ccNo", sumData.getCcNo());
            json.put("cydw", sumData.getCydw());
            json.put("danPrice", sumData.getDanPrice() != null ? sumData.getDanPrice().stripTrailingZeros().toPlainString() : null);
            json.put("dataFrom", sumData.getDataFrom());
            json.put("douShu", sumData.getDouShu() != null ? sumData.getDouShu().stripTrailingZeros().toPlainString() : null);
            json.put("dwmc", sumData.getDwmc());
            json.put("gross", sumData.getGross() != null ? sumData.getGross().stripTrailingZeros().toPlainString() : null);
            json.put("lastWeigh", sumData.getLastWeigh() != null ? (sumData.getLastWeigh()?ONE:ZERO) : null);
            json.put("loader", sumData.getLoader());
            json.put("materialName", sumData.getMaterialName());
            json.put("operator", sumData.getOperator());
            json.put("orderNo", sumData.getOrderNo());
            json.put("remf0", sumData.getRemf0() != null ? sumData.getRemf0().stripTrailingZeros().toPlainString() : null);
            json.put("remf1", sumData.getRemf1() != null ? sumData.getRemf1().stripTrailingZeros().toPlainString() : null);
            json.put("remf2", sumData.getRemf2() != null ? sumData.getRemf2().stripTrailingZeros().toPlainString() : null);
            json.put("remf3", sumData.getRemf3() != null ? sumData.getRemf3().stripTrailingZeros().toPlainString() : null);
            json.put("remf4", sumData.getRemf4() != null ? sumData.getRemf4().stripTrailingZeros().toPlainString() : null);
            json.put("remf5", sumData.getRemf5() != null ? sumData.getRemf5().stripTrailingZeros().toPlainString() : null);
            json.put("remf6", sumData.getRemf6() != null ? sumData.getRemf6().stripTrailingZeros().toPlainString() : null);
            json.put("remf7", sumData.getRemf7() != null ? sumData.getRemf7().stripTrailingZeros().toPlainString() : null);
            json.put("remf8", sumData.getRemf8() != null ? sumData.getRemf8().stripTrailingZeros().toPlainString() : null);
            json.put("remf9", sumData.getRemf9() != null ? sumData.getRemf9().stripTrailingZeros().toPlainString() : null);
            json.put("remn0", sumData.getRemn0() != null ? sumData.getRemn0().stripTrailingZeros().toPlainString() : null);
            json.put("remn1", sumData.getRemn1() != null ? sumData.getRemn1().stripTrailingZeros().toPlainString() : null);
            json.put("remn2", sumData.getRemn2() != null ? sumData.getRemn2().stripTrailingZeros().toPlainString() : null);
            json.put("remn3", sumData.getRemn3() != null ? sumData.getRemn3().stripTrailingZeros().toPlainString() : null);
            json.put("remn4", sumData.getRemn4() != null ? sumData.getRemn4().stripTrailingZeros().toPlainString() : null);
            json.put("rems0", sumData.getRems0());
            json.put("rems1", sumData.getRems1());
            json.put("rems2", sumData.getRems2());
            json.put("rems3", sumData.getRems3());
            json.put("rems4", sumData.getRems4());
            json.put("rems5", sumData.getRems5());
            json.put("rems6", sumData.getRems6());
            json.put("rems7", sumData.getRems7());
            json.put("rems8", sumData.getRems8());
            json.put("rems9", sumData.getRems9());
            json.put("scaleNo", sumData.getScaleNo() != null ? sumData.getScaleNo().stripTrailingZeros().toPlainString() : null);
            json.put("shFlag", sumData.getShFlag() != null ? (sumData.getShFlag()?ONE:ZERO) : null);
            json.put("suttle", sumData.getSuttle() != null ? sumData.getSuttle().stripTrailingZeros().toPlainString() : null);
            json.put("synchroFlag", sumData.getSynchroFlag() != null ? (sumData.getSynchroFlag()?ONE:ZERO) : null);
            json.put("tare", sumData.getTare() != null ? sumData.getTare().stripTrailingZeros().toPlainString() : null);
            json.put("totalWeight", sumData.getTotalWeight() != null ? sumData.getTotalWeight().stripTrailingZeros().toPlainString() : null);
            json.put("weightDate", sumData.getWeightDate()==null?null:String.valueOf(sumData.getWeightDate().getTime()));
            json.put("weightTime", DateUtil.format(sumData.getWeightTime(), "yyyy-MM-dd HH:mm:ss"));
            json.put("ysgj", sumData.getYsgj());
            json.put("ziDong", sumData.getZiDong() != null ? (sumData.getZiDong()?ONE:ZERO) : null);
            log.info("====={}=====：{}--{}", message, sumData.getId(), json);
            Map<String, String> headerMap = new HashMap<>();
            headerMap.put("token", token.getToken());
            headerMap.put("Content-Type", "application/json");
            String body = HttpRequest.post(pushURL)
                    .body(json.toJSONString())
                    .addHeaders(headerMap)
                    .timeout(20000)
                    .execute()
                    .body();
            log.info("====={}结果=====：{}--{}", message, sumData.getId(), body);
            BaseResult<String> result = JSONObject.parseObject(body, BaseResult.class);
            return result;
        } catch (Exception e) {
            log.info("====={}异常=====", message);
            throw new RuntimeException(e);
        }
    }
}
