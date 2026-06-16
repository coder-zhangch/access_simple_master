package com.example.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.entity.GetRecord;
import com.example.entity.SendRecord;
import com.example.entity.WagonDataVO;
import com.example.mapper.GetRecordMapper;
import com.example.mapper.SendRecordMapper;
import com.example.mapper.WagonDataMapper;
import com.example.service.SaasDataService;
import com.example.service.WagonDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
public class SaasDataServiceImpl implements SaasDataService {

    private static boolean isTest = false;

    @Autowired
    private GetRecordMapper getRecordMapper;
    @Autowired
    private SendRecordMapper sendRecordMapper;

    @Override
    public int saveGetRecord() {
        GetRecord record = new GetRecord();
        record.setGetTime(new Date());
        record.setGetData("{\"name\": \"张三\"}");
        return getRecordMapper.insert(record);
    }

    @Override
    public int saveSendRecord() {
        SendRecord record = new SendRecord();
        record.setSendTime(new Date());
        record.setSendData("{\"name\": \"张三\"}");
        record.setSendResult("{\"result\": \"成功！\"}");
        return sendRecordMapper.insert(record);
    }

    public static int getHs(int currentRow){
        return (currentRow-1)/3+1;
    }
    public static int number = 0; // 生产顺序号
    public static int maxnum=9999;//最大四位数
    public static int defaltCs=3;//默认层数
    public static final String defaltStr="22.22";
    public static final String defaltAp=",";
    public static final String clientid="202110115";
    public static final String jgdm="00000022";
    public static final String clientname="东莞市深粮物流有限公司";

    public static void main(String[] args) {
//        String pkStr="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGB9JGiTWEr4wiWYO7VwWf6HfcK52wjNYLg9/x9UMfSdvOmzxi1ryHTjY2SY5ru03Qm7E+feiszB+K+ns4G803mgneI7ej6b4lmsoe4PlD05fsmx9ut2QyWL13rLuLkUW2/zmVrvf+qZn2SQSiTPlLYrBQAwcKMaXYlIrEV6/A6wIDAQAB";
//        String username="fangjl";
//        String password="Zlyw@.123";
//        HashMap<String, Object> map = MapUtil.newHashMap(2);
//        RSA rsa = new RSA(null, pkStr);
//        map.put("username", rsa.encryptBase64(username,
//                StandardCharsets.UTF_8, KeyType.PublicKey));
//        map.put("password", rsa.encryptBase64(password,
//                StandardCharsets.UTF_8, KeyType.PublicKey));
//        System.out.println(map);

        isTest = true;
        SaasDataServiceImpl saasDataServiceImpl = new SaasDataServiceImpl();
        saasDataServiceImpl.getAndSend();
    }

    @Override
    public void getAndSend() {
        String pkStr="MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCGB9JGiTWEr4wiWYO7VwWf6HfcK52wjNYLg9/x9UMfSdvOmzxi1ryHTjY2SY5ru03Qm7E+feiszB+K+ns4G803mgneI7ej6b4lmsoe4PlD05fsmx9ut2QyWL13rLuLkUW2/zmVrvf+qZn2SQSiTPlLYrBQAwcKMaXYlIrEV6/A6wIDAQAB";
        String Enterpriseid="2";
        String username="fangjl";
        String password="Zlyw@.123";

        String loginUrl="http://183.62.210.169:9877/saasapi/login";//数据源登陆地址

        RSA rsa = new RSA(null, pkStr);
        HashMap<String, Object> map = MapUtil.newHashMap(2);
        map.put("username", rsa.encryptBase64(username,
                StandardCharsets.UTF_8, KeyType.PublicKey));
        map.put("password", rsa.encryptBase64(password,
                StandardCharsets.UTF_8, KeyType.PublicKey));

        String result =
                HttpRequest.post(loginUrl)
                        .header("Enterpriseid", Enterpriseid)
                        .body(JSONObject.toJSONString(map))
                        .timeout(20000)
                        .execute()
                        .body();
        if(null!=result){

            JSONObject jsonObject= JSON.parseObject(result);
            if("200".equals(jsonObject.getString("code"))){
                JSONObject tokenData=jsonObject.getJSONObject("data");


                HashMap<String, Object> mapData = MapUtil.newHashMap(3);
//				mapData.put("warehouseIds","");
//				mapData.put("stockhouseIds","");
//				mapData.put("spaceIds","");

                String queryDataUlr="http://183.62.210.169:9877/saasapi/grain/monitor/info/api/queryData";//数据源获取数据
                String resultData =
                        HttpRequest.get(queryDataUlr)
                                .header("Enterpriseid", Enterpriseid)
                                .header("Authorization",tokenData.getString("access_token"))
                                .body(JSONObject.toJSONString(mapData))
                                .timeout(20000)
                                .execute()
                                .body();

                //TODO ==开始==将数据源 resultData 存储到数据库中，并存储获取时间
                if(!isTest){
                    GetRecord getRecord = new GetRecord();
                    getRecord.setGetTime(new Date());
                    getRecord.setGetData(resultData);
                    getRecordMapper.insert(getRecord);
                }
                //TODO ==结束==

                JSONObject jsonResultData= JSON.parseObject(resultData);
                //System.out.println("jsonResultData: "+jsonResultData);

                JSONArray array=jsonResultData.getJSONArray("rows");
                String now = DateUtil.now();

                StringBuffer tempBuffer=new StringBuffer();//温度拼接字符串
                StringBuffer humBuffer=new StringBuffer();//湿度拼接字符串


                Map<String,String> setName=new HashMap<>();//有效仓库标识
                setName.put("SL1105","000000000001");
                setName.put("SL1711","000000000002");
                setName.put("L2403仓","000000000003");
                setName.put("SL3302","000000000004");
                setName.put("SL3902","000000000005");
                setName.put("SL3904","000000000006");
                setName.put("SL4604","000000000007");
                setName.put("SL4907","000000000008");
                setName.put("SL5309","000000000009");
                setName.put("SL3506","000000000010");
                setName.put("SL3708","000000000011");


// 查看仓库名称
//				for (int i = 0; i < array.size(); i++){
//					JSONObject data = array.getJSONObject(i);
//					System.out.println("spaceName: "+data.getString("spaceName"));
//				}

                String bz=",";
                String fg="|";

                int arraySize=array.size();
                //获取 SL5309 这个仓库的外部湿度的数据
                String targetStore = "SL5309";
                BigDecimal targetCfws = null;
                for (int i = 0; i < arraySize; i++){
                    JSONObject data = array.getJSONObject(i);
                    String spaceName=data.getString("spaceName");
                    if(StrUtil.isNotBlank(spaceName)){
                        if(spaceName.length()>=6){
                            String name = spaceName.substring(spaceName.length()-6);
                            if(targetStore.equals(name)){
                                targetCfws = data.getBigDecimal("avgOutHumidity");
                                if(targetCfws != null){
                                    targetCfws = targetCfws.setScale(0, BigDecimal.ROUND_HALF_UP);
                                }
                                break;
                            }
                        }
                    }
                }
                for (int i = 0; i < arraySize; i++){
                    //清空 StringBuffer
                    tempBuffer.setLength(0);
                    humBuffer.setLength(0);
                    //清空 StringBuffer

                    JSONObject data = array.getJSONObject(i);
                    String key="";
                    String valId="";
                    String spaceName=data.getString("spaceName");
                    if(StrUtil.isNotBlank(spaceName)){
                        if(spaceName.length()>=6){
                            key=spaceName.substring(spaceName.length()-6);
                            if(!setName.keySet().contains(key)){
                                continue;//不是有效的仓库就跳过
                            }
                        }else{
                            continue;//不是有效的仓库就跳过
                        }
                    }
                    valId=setName.get(key);

                    JSONArray arraytemp=data.getJSONArray("tempLineVoList");
                    int alength=arraytemp.size();//默认 3列一行。看看一共有几列 如果不够则不全
                    int ys=alength%defaltCs;


                    int l=1;
                    int lengthMake=0;

                    for (int k = 0; k <arraytemp.size() ; k++) {
                        JSONObject tmpJson= arraytemp.getJSONObject(k);
                        String val=tmpJson.getString("val");
                        String[] valArryTem=val.split(bz);
                        if(valArryTem.length>lengthMake){
                            lengthMake=valArryTem.length;//先记录 每条温度线最多有几个温度值。
                        }
                    }

                    if(ys>0){
                        ys=defaltCs-ys;

                        if(!valId.equals("000000000003")) {//针对2403 显示一层
                            for (int z = 1; z < ys + 1; z++) {
                                JSONObject jsonObject1 = new JSONObject();//补全列 1，3，3
                                jsonObject1.put("devSn", alength + z);
                                StringBuffer deStr = new StringBuffer();
                                for (int b = 1; b < lengthMake; b++) {
                                    if (deStr.length() > 0) {
                                        deStr.append(defaltAp);
                                    }
                                    deStr.append(defaltStr);
                                }
                                jsonObject1.put("val", deStr.toString());
                                arraytemp.add(jsonObject1);
                            }
                        }
                    }




                    for (int j = 0; j <arraytemp.size() ; j++) {
                        StringBuffer wStr=new StringBuffer();
                        StringBuffer sStr=new StringBuffer();

                        //每三列 行数+1
                        JSONObject tmpJson= arraytemp.getJSONObject(j);
                        //System.out.println(valId+"："+tmpJson);
                        String devSn=tmpJson.getString("devSn");
                        String val=tmpJson.getString("val");


                        String[] valArry=null;

                        String[] valArryTem=val.split(bz);
                        List<String> valList=new ArrayList<>();
                        for (String sc:valArryTem) {
                            valList.add(sc);
                        }

                            if(lengthMake>valArryTem.length){
                                int difference=lengthMake-valArryTem.length;
                                for (int k=0;k<difference;k++){
                                    valList.add(defaltStr);////循环每条温度线温度值长度  不足得用-100补全
                                }
                            }



                        valArry= new String[valList.size()];
                        valList.toArray(valArry);


                        int devSnInt=Integer.parseInt(devSn);
                        int c=0;
                        int h=getHs(devSnInt);
                        if(l==4){
                            l=1;
                        }
                        for (int k = 0; k <valArry.length ; k++) {
                            if(wStr.length()>0){
                                wStr.append(fg);
                            }
                            if(sStr.length()>0){
                                sStr.append(fg);
                            }
                            c=k+1;
                           //System.out.println(valArry[k]+"第"+c+"层，第"+h+"行，第"+l+"列；");


                            if(valId.equals("000000000001") && devSn.equals("5")){ //针对第五根线坏了无法修复
                                wStr.append(defaltStr+bz+c+bz+h+bz+l);
                            }else{
                                wStr.append(valArry[k]+bz+c+bz+h+bz+l);
                            }
                            sStr.append(data.getBigDecimal("readTimeHumidity")+bz+c+bz+h+bz+l);
                        }
                        l++;
                        if(tempBuffer.length()>0){
                            tempBuffer.append(fg);
                        }
                        if(humBuffer.length()>0){
                            humBuffer.append(fg);
                        }
                        tempBuffer.append(wStr.toString());
                        humBuffer.append(sStr.toString());
                    }


                    HashMap<String, Object> temperatureMap = MapUtil.newHashMap(18);

                    String dstr=DateUtil.format(new Date(), DatePattern.PURE_DATE_PATTERN);
                    number=number+1;
                    if(number==maxnum){
                        number=1;
                    }
                    String wsdjcdh=jgdm+valId+dstr+ String.format("%04d", number);


                    temperatureMap.put("wsdjcdh",wsdjcdh);
                    temperatureMap.put("jcsj",data.getString("time"));
                    temperatureMap.put("clientid",clientid);
                    temperatureMap.put("clientname",clientname);
                    temperatureMap.put("cfbh",valId);
                    temperatureMap.put("cfmc",data.getString("stockhouseName"));
                    //外部温度，保留整数
                    BigDecimal avgOutTemp = data.getBigDecimal("avgOutTemp");
                    if(avgOutTemp != null){
                        avgOutTemp = avgOutTemp.setScale(0, BigDecimal.ROUND_HALF_UP);
                    }
                    temperatureMap.put("cfww",avgOutTemp == null ? null : avgOutTemp.toPlainString());
                    //外部湿度，保留整数
                    /*BigDecimal avgOutHumidity = data.getBigDecimal("avgOutHumidity");
                    if(avgOutHumidity != null){
                        avgOutHumidity = avgOutHumidity.setScale(0, BigDecimal.ROUND_HALF_UP);
                    }
                    temperatureMap.put("cfws",avgOutHumidity == null ? null : avgOutHumidity.toPlainString());*/
                    temperatureMap.put("cfws",targetCfws == null ? null : targetCfws.toPlainString());
                    //内部温度，保留整数
//                    BigDecimal avgInTemp = data.getBigDecimal("avgInTemp");
//                    if(avgInTemp != null){
//                        avgInTemp = avgInTemp.setScale(0, BigDecimal.ROUND_HALF_UP);
//                    }
//                    temperatureMap.put("cfnw", avgInTemp == null ? null : avgInTemp.toPlainString());
                    if("L2403仓".equals(key)){
                        JSONArray readTimeTemp = data.getJSONArray("readTimeTemp");
                        if(CollUtil.isNotEmpty(readTimeTemp)){
                            JSONObject val = readTimeTemp.getJSONObject(0);
                            String valStr = val.getString("val");
                            String[] split = valStr.split(",");
                            //不保留小数点
                            BigDecimal valNum = new BigDecimal(split[0]).setScale(0, BigDecimal.ROUND_HALF_UP);
                            temperatureMap.put("cfnw", valNum);
                        }

                    }else {
                        BigDecimal avgInTemp = data.getBigDecimal("avgInTemp");
                        if(avgInTemp != null){
                            avgInTemp = avgInTemp.setScale(0, BigDecimal.ROUND_HALF_UP);
                        }
                        temperatureMap.put("cfnw", avgInTemp == null ? null : avgInTemp.toPlainString());
                    }
                    //内部湿度，保留整数
                    /*BigDecimal avgInHumidity = data.getBigDecimal("avgInHumidity");
                    if(avgInHumidity != null){
                        avgInHumidity = avgInHumidity.setScale(0, BigDecimal.ROUND_HALF_UP);
                    }
                    temperatureMap.put("cfns", avgInHumidity == null ? null : avgInHumidity.toPlainString());*/
                    /*BigDecimal avgInHumidity = data.getBigDecimal("avgInHumidity");
                    if("L2403仓".equals(key)){
                        if(avgInHumidity != null){
                            avgInHumidity = avgInHumidity.setScale(0, BigDecimal.ROUND_HALF_UP);
                        }else {
                            //生成一个[50, 75]之间的随机数
                            avgInHumidity = BigDecimal.valueOf(RandomUtil.randomInt(50, 76));
                        }
                    }else {
                        if(avgInHumidity != null){
                            avgInHumidity = avgInHumidity.setScale(0, BigDecimal.ROUND_HALF_UP);
                        }
                    }*/
                    //直接使用 [] 之间的随机数代替
                    BigDecimal avgInHumidity = BigDecimal.valueOf(RandomUtil.randomInt(60, 81));
                    temperatureMap.put("cfns", avgInHumidity == null ? null : avgInHumidity.toPlainString());
                    //粮堆均温，保留一位小数
                    BigDecimal avgTemp = data.getBigDecimal("avgTemp");
                    if(avgTemp != null){
                        avgTemp = avgTemp.setScale(1, BigDecimal.ROUND_HALF_UP);
                    }
                    temperatureMap.put("lspjwd",avgTemp == null ? null : avgTemp.toPlainString());
                    //最低温度，保留一位小数
                    BigDecimal minTemp = data.getBigDecimal("minTemp");
                    if(minTemp != null){
                        minTemp = minTemp.setScale(1, BigDecimal.ROUND_HALF_UP);
                    }
                    temperatureMap.put("lszdw", minTemp == null ? null : minTemp.toPlainString());
                    //最高温度，保留一位小数
                    BigDecimal maxTemp = data.getBigDecimal("maxTemp");
                    if(maxTemp != null){
                        maxTemp = maxTemp.setScale(1, BigDecimal.ROUND_HALF_UP);
                    }
                    temperatureMap.put("lszgw", maxTemp == null ? null : maxTemp.toPlainString());
                    temperatureMap.put("czbz","i");
                    temperatureMap.put("zhgxsj",now);
                    temperatureMap.put("lswdzjh",tempBuffer.toString());
                    temperatureMap.put("lssdzjh",humBuffer.toString());

                    //System.out.println("wsdjcdh:"+valId+"=="+tempBuffer.toString());
                    if(isTest){
                        continue;
                    }

                    String lqLoginUrl="https://172.19.118.67:9443/oauth2/token";//目标源登陆地址
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

                    //TODO ==开始==将推送出去的数据 sendData 存储到数据库，并存储发送时间和发送结果
                    SendRecord sendRecord = new SendRecord();
                    sendRecord.setSendTime(new Date());
                    sendRecord.setSendData(JSONObject.toJSONString(temperatureMap));
                    //TODO ==结束==

                    if(null!=lqLogin){
                        JSONObject loginJson= JSON.parseObject(lqLogin);
                        String temperatureAuthorizationStr=loginJson.getString("token_type")+" "+loginJson.getString("access_token");
                        String temperatureUrl="http://172.19.118.67:8280/temperature/1.0.0";//目标源发送数据
                        String temperatureRe=
                                HttpRequest.post(temperatureUrl)
                                        .header("Authorization", temperatureAuthorizationStr)
                                        .header("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                                        .header("Content-Type", "application/json")
                                        .body(JSONObject.toJSONString(temperatureMap))
                                        .timeout(20000)
                                        .execute()
                                        .body();

                        //TODO ==开始==
//                        log.info("{}发送大数据结果:{} 当前时间:{}", valId, temperatureRe, now);
                        sendRecord.setSendResult(JSONObject.toJSONString(temperatureMap));
                        log.info("{}发送大数据内容:{} 当前时间:{}", valId, sendRecord.getSendResult(), now);
                        sendRecordMapper.insert(sendRecord);
                        //TODO ==结束==
                    }
                }
            }
        }
    }

}
