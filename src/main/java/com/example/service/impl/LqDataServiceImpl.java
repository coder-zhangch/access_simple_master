package com.example.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.LqData;
import com.example.mapper.LqDataMapper;
import com.example.service.LqDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LqDataServiceImpl implements LqDataService {

    @Autowired
    private LqDataMapper lqDataMapper;

    @Override
    public LqData getLastBy(String cfbh) {
        return lqDataMapper.getLastBy(cfbh);
    }

    @Override
    public LqData getLastByDate(String cfbh, String start, String end) {
        return lqDataMapper.getLastByDate(cfbh, start, end);
    }

    @Override
    public LqData getLastByCfbh(String cfbh) {
        return lqDataMapper.getLastByCfbh(cfbh);
    }

    public static void main(String[] args) {
        String original="29.561,1,1,1,1|30.816,2,1,1,1|30.758,3,1,1,1|26.81,4,1,1,1|25.81,5,1,1,1|25.44,6,1,1,1|25.25,7,1,1,1|25.19,8,1,1,1|25.31,9,1,1,1|25.12,10,1,1,1|24.88,11,1,1,1|24.88,12,1,1,1|24.75,13,1,1,1|24.75,14,1,1,1|24.69,15,1,1,1|30.75,1,1,2,2|30.06,2,1,2,2|26.38,3,1,2,2|25.75,4,1,2,2|25.31,5,1,2,2|25.19,6,1,2,2|25.38,7,1,2,2|25.62,8,1,2,2|25.38,9,1,2,2|25.19,10,1,2,2|24.88,11,1,2,2|24.62,12,1,2,2|25.19,13,1,2,2|30.81,1,1,3,3|30.56,2,1,3,3|26.44,3,1,3,3|25.94,4,1,3,3|25.75,5,1,3,3|26.06,6,1,3,3|26.12,7,1,3,3|26.44,8,1,3,3|26.06,9,1,3,3|25.69,10,1,3,3|25.25,11,1,3,3|25.06,12,1,3,3|25.75,13,1,3,3|28.56,1,1,4,4|30.56,2,1,4,4|26.56,3,1,4,4|26.06,4,1,4,4|25.19,5,1,4,4|25.25,6,1,4,4|25.69,7,1,4,4|25.44,8,1,4,4|25.44,9,1,4,4|25.12,10,1,4,4|24.56,11,1,4,4|24.31,12,1,4,4|24.62,13,1,4,4|30.75,1,1,5,5|30.06,2,1,5,5|26.31,3,1,5,5|25.56,4,1,5,5|25.12,5,1,5,5|25.19,6,1,5,5|25.44,7,1,5,5|" +
                "25.56,8,1,5,5|25.25,9,1,5,5|24.94,10,1,5,5|24.62,11,1,5,5|24.56,12,1,5,5|24.88,13,1,5,5";
        String convertStr = getConvertStr(original);
        System.out.println(convertStr);
    }

    @Override
    public void dataPush(LqData lqData){
        //这个是sqlserver 读出来的 lswdzjh
//        String original="29.56,1,1,1,1|30.81,2,1,1,1|30.75,3,1,1,1|26.81,4,1,1,1|25.81,5,1,1,1|25.44,6,1,1,1|25.25,7,1,1,1|25.19,8,1,1,1|25.31,9,1,1,1|25.12,10,1,1,1|24.88,11,1,1,1|24.88,12,1,1,1|24.75,13,1,1,1|24.75,14,1,1,1|24.69,15,1,1,1|30.75,1,1,2,2|30.06,2,1,2,2|26.38,3,1,2,2|25.75,4,1,2,2|25.31,5,1,2,2|25.19,6,1,2,2|25.38,7,1,2,2|25.62,8,1,2,2|25.38,9,1,2,2|25.19,10,1,2,2|24.88,11,1,2,2|24.62,12,1,2,2|25.19,13,1,2,2|30.81,1,1,3,3|30.56,2,1,3,3|26.44,3,1,3,3|25.94,4,1,3,3|25.75,5,1,3,3|26.06,6,1,3,3|26.12,7,1,3,3|26.44,8,1,3,3|26.06,9,1,3,3|25.69,10,1,3,3|25.25,11,1,3,3|25.06,12,1,3,3|25.75,13,1,3,3|28.56,1,1,4,4|30.56,2,1,4,4|26.56,3,1,4,4|26.06,4,1,4,4|25.19,5,1,4,4|25.25,6,1,4,4|25.69,7,1,4,4|25.44,8,1,4,4|25.44,9,1,4,4|25.12,10,1,4,4|24.56,11,1,4,4|24.31,12,1,4,4|24.62,13,1,4,4|30.75,1,1,5,5|30.06,2,1,5,5|26.31,3,1,5,5|25.56,4,1,5,5|25.12,5,1,5,5|25.19,6,1,5,5|25.44,7,1,5,5|25.56,8,1,5,5|25.25,9,1,5,5|24.94,10,1,5,5|24.62,11,1,5,5|24.56,12,1,5,5|24.88,13,1,5,5";
        //这个是sqlserver 读出来的 lssdzjh

        String lswdzjhStr= getConvertStr(lqData.getLswdzjh());
        String lssdzjhStr= getConvertStr(lqData.getLswdzjh());

        HashMap<String, Object> temperatureMap = MapUtil.newHashMap(18);

        String dstr= DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
        number=number+1;
        if(number==maxnum){
            number=1;
        }

        Map<String,String> cfbhMap=new HashMap();
        cfbhMap.put("1-2","000000000001");
        cfbhMap.put("2-2","000000000002");
        cfbhMap.put("3-2","000000000003");

        String valId=cfbhMap.get(lqData.getCfbh());

        String wsdjcdh=jgdm+valId+dstr+ String.format("%04d", number);
        String now = DateUtil.now();

        temperatureMap.put("wsdjcdh",wsdjcdh);
        temperatureMap.put("jcsj",DateUtil.format(lqData.getJcsj(), "yyyy-MM-dd HH:mm:ss"));//jcsj
        temperatureMap.put("clientid",clientid);
        temperatureMap.put("clientname",clientname);
        temperatureMap.put("cfbh",valId);
        temperatureMap.put("cfmc",lqData.getCfmc());
        //仓房外温
        temperatureMap.put("cfww",lqData.getCfww() != null ? new BigDecimal(lqData.getCfww()).setScale(1, BigDecimal.ROUND_DOWN) : lqData.getCfww());//cfww
        //仓房外湿
        temperatureMap.put("cfws",lqData.getCfws() != null ? new BigDecimal(lqData.getCfws()).setScale(1, BigDecimal.ROUND_DOWN) : lqData.getCfws());//cfws
        //仓房内温
        temperatureMap.put("cfnw",lqData.getCfnw() != null ? new BigDecimal(lqData.getCfnw()).setScale(1, BigDecimal.ROUND_DOWN) : lqData.getCfnw());//cfnw
        //仓房内湿
        temperatureMap.put("cfns",lqData.getCfns() != null ? new BigDecimal(lqData.getCfns()).setScale(1, BigDecimal.ROUND_DOWN) : lqData.getCfns());//cfns
        //粮食平均温度
        temperatureMap.put("lspjwd",lqData.getLspjwd() != null ? new BigDecimal(lqData.getLspjwd()).setScale(1, BigDecimal.ROUND_DOWN) : lqData.getLspjwd());//lspjwd
        //粮食最低温
        temperatureMap.put("lszdw",lqData.getLszdw() != null ? new BigDecimal(lqData.getLszdw()).setScale(1, BigDecimal.ROUND_DOWN) : lqData.getLszdw());//lszdw
        //粮食最高温
        temperatureMap.put("lszgw",lqData.getLszgw() != null ? new BigDecimal(lqData.getLszgw()).setScale(1, BigDecimal.ROUND_DOWN) : lqData.getLszgw());//lszgw
        temperatureMap.put("czbz","i");
        temperatureMap.put("zhgxsj",now);
        temperatureMap.put("lswdzjh",lswdzjhStr);
        temperatureMap.put("lssdzjh",lssdzjhStr);

        //	System.out.println("wsdjcdh:"+valId+"=="+tempBuffer.toString());

        String lqLoginUrl="https://192.168.6.17:9443/oauth2/token";//目标源登陆地址
        String lqAuthorizationStr="Basic RmFxVTVNMWF4SW5Md1R5cUZHR182T2ZfU0pVYTpHN3hwTlp2OHozaHFZeHJFYUVzVlZlTVd6WTBh";
        HashMap<String, Object> lqMapLogin = MapUtil.newHashMap(3);
        lqMapLogin.put("grant_type","password");
        lqMapLogin.put("username","admin");
        lqMapLogin.put("password","admin");
        String lqLogin =
                HttpRequest.post(lqLoginUrl)
                        .header("Authorization", lqAuthorizationStr)
                        .body(JSONObject.toJSONString(lqMapLogin))
                        .timeout(20000)
                        .execute()
                        .body();

        if(null!=lqLogin){
            String valueJson = JSONObject.toJSONString(temperatureMap);
//            System.out.println("发送大数据内容:"+valueJson);
            log.info("1发送大数据内容:{}", valueJson);
            JSONObject loginJson= JSON.parseObject(lqLogin);
            String temperatureAuthorizationStr=loginJson.getString("token_type")+" "+loginJson.getString("access_token");
            String temperatureUrl="http://192.168.6.17:8280/temperature/1.0.0";//目标源发送数据
            String temperatureRe=
                    HttpRequest.post(temperatureUrl)
                            .header("Authorization", temperatureAuthorizationStr)
                            .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                            .header("Content-Type", "application/json")
                            .body(valueJson)
                            .timeout(20000)
                            .execute()
                            .body();
//            System.out.println("发送大数据结果:"+temperatureRe);
            log.info("2发送大数据结果:{}", temperatureRe);
        }
    }

    //拿到数据转换实际字符串
    public static String getConvertStr(String original){
        // 分割原始字符串
        String[] parts = original.split("\\|");

        //TODO 2026-03-24改动：lswdzjh每位的第一个数保留2位小数
        try {
            for (int i = 0; i < parts.length; i++) {
                String tier = parts[i];
                String[] tierSplit = tier.split(",");
                String wd = tierSplit[0];
                BigDecimal wdDecimal = new BigDecimal(wd).setScale(2, BigDecimal.ROUND_HALF_UP);
                tierSplit[0] = wdDecimal.toString();
                String tierStr = Arrays.stream(tierSplit).collect(Collectors.joining(","));
                parts[i] = tierStr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //TODO 2026-03-10改动：获取lswdzjh最后四位的平均值
        String defStr="22,";
        /*try {
            BigDecimal wdTotal = BigDecimal.ZERO;
            for (int i = parts.length-4; i < parts.length; i++) {
                String tier = parts[i];
                String[] tierSplit = tier.split(",");
                String wd = tierSplit[0];
                wdTotal = wdTotal.add(new BigDecimal(wd));
            }
            defStr = wdTotal.divide(BigDecimal.valueOf(4), 2, BigDecimal.ROUND_HALF_UP).toString() + ",";
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        List<String> list = new ArrayList<>(Arrays.asList(parts));

        String fg="|";
        String index1="1,1,1";
        String index2="1,2,2";
        String index3="1,3,3";
        String index4="1,4,4";
        String index5="1,5,5";
        List<String> vl1=new ArrayList<>();
        List<String> vl2=new ArrayList<>();
        List<String> vl3=new ArrayList<>();
        List<String> vl4=new ArrayList<>();
        List<String> vl5=new ArrayList<>();
        for (String src:list) {
            if (src.endsWith(index1)) {
                vl1.add(src);
            }
            if (src.endsWith(index2)) {
                vl2.add(src);
            }
            if (src.endsWith(index3)) {
                vl3.add(src);
            }
            if (src.endsWith(index4)) {
                vl4.add(src);
            }
            if (src.endsWith(index5)) {
                vl5.add(src);
            }
        }

        int vl1size=vl1.size();
        int vl2size=vl2.size();
        int vl3size=vl3.size();
        int vl4size=vl4.size();
        int vl5size=vl5.size();
        // 找出最大值
        int maxSize = Math.max(vl1size, Math.max(vl2size, Math.max(vl3size, Math.max(vl4size, vl5size))));

//        String defStr="-100,";
        //补全格式
        if(maxSize>vl1.size()){
            for (;maxSize>vl1size;){
                int i=vl1size+1;
                vl1size++;
            }
        }
        if(maxSize>vl2.size()){
            for (;maxSize>vl2size;){
                int i=vl2size+1;
                vl2.add(defStr+i+","+index2);
                vl2size++;
            }
        }
        if(maxSize>vl3.size()){
            for (;maxSize>vl3size;){
                int i=vl3size+1;
                vl3.add(defStr+i+","+index3);
                vl3size++;
            }
        }
        if(maxSize>vl4.size()){
            for (;maxSize>vl4size;){
                int i=vl4size+1;
                vl4.add(defStr+i+","+index4);
                vl4size++;
            }
        }
        if(maxSize>vl5.size()){
            for (;maxSize>vl5size;){
                int i=vl5size+1;
                vl5.add(defStr+i+","+index5);
                vl5size++;
            }
        }

//        for (String src:vl2
//        ) {
//            System.out.println(src);
//        }
        StringBuffer sb=new StringBuffer();
        int h=1;
        int c=1;
        for (int i = 0; i < maxSize; i++) {

            sb.append(clStr(vl1.get(i))+dh+h+dh+c+dh+"1"+fg+
                    clStr(vl2.get(i))+dh+h+dh+c+dh+"2"+fg+
                    clStr(vl3.get(i))+dh+h+dh+c+dh+"3"+fg+
                    clStr(vl4.get(i))+dh+h+dh+c+dh+"4"+fg+
                    clStr(vl5.get(i))+dh+h+dh+c+dh+"5"+fg);
            h++;
        }
        return sb.toString();
    }
    public static int number = 0; // 生产顺序号
    public static int maxnum=9999;//最大四位数
    public static final String clientid="20211212";
    public static final String jgdm="00000022";
    public static final String clientname="东莞市国丰粮油有限公司";


    public static final String dh=",";
    public static String clStr(String srt){
        return srt.substring(0,srt.indexOf(dh));
    }
}
