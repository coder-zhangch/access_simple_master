package com.example.task;

import cn.hutool.core.collection.CollUtil;
import com.example.entity.ListVideoDataVO;
import com.example.entity.WagonDataVO;
import com.example.entity.WeighingData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.ListVideoDataService;
import com.example.service.SaasDataService;
import com.example.service.WagonDataService;
import com.example.service.WeighingDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 将最后推送的id和推送失败的id缓存到redis中
 */
@Component
@Slf4j
public class ScaleRedisTask {

    private final static String dataPush = "散料 Data Push: ";
    private final static String wagonDataPush = "地磅 Data Push: ";
    private final static String failDataPush = "Error Data Push: ";
    private final static String comma = ",";

    @Autowired
    private WeighingDataService weighingDataService;

    @Autowired
    private WagonDataService wagonDataService;

    @Autowired
    private ListVideoDataService listVideoDataService;

    @Autowired
    private SaasDataService saasDataService;

    //上午8点和下午三点分别推送一次
    @Async
    @Scheduled(cron = "0 0 9 * * ?")
    public void run9(){
        saasDataService.getAndSend();
    }
//    @Async
//    @Scheduled(cron = "0 0 15 * * ?")
//    public void run15(){
//        saasDataService.getAndSend();
//    }

    @Async
//    @Scheduled(cron = "0 */5 * * * ? ")
    @Scheduled(cron = "0 */10 * * * ? ")
    public void push(){
        //散料 数据推送
        pushData();
        //等10秒
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //地磅 数据推送
        wagonPushData();
    }

    //散料数据推送，当有lastWeight=0的数据时，通过秤号查询同一批次的数据，整合成一条后再推送
    public void pushData(){
        //查询未推送的，且lastWeight = 0的所有数据
        List<WeighingData> unpushedList = weighingDataService.getUnpushed();
        if(CollUtil.isEmpty(unpushedList)){
            return;
        }
        //通过上面未推送的数据列表，获取所有秤号相同的数据，所有订单编号相同的数据为同一批次
        List<String> orderNoList = unpushedList.stream()
                .filter(item -> item.getOrderNo() != null)
                .map(WeighingData::getOrderNo)
                .collect(Collectors.toList());
        //秤号对应数据记录，除重量需要累计外，其他的均使用 lastWeigh=0 的这条数据的值
        Map<String, WeighingData> dataMap = unpushedList.stream()
                .filter(item -> item.getOrderNo() != null)
                .collect(Collectors.toMap(WeighingData::getOrderNo, Function.identity(), (v1, v2) -> v1));
        List<WeighingData> dataList = weighingDataService.queryByOrderNo(orderNoList);
        //通过orderNo分组
        Map<String, List<WeighingData>> groupMap = dataList.stream()
                .collect(Collectors.groupingBy(WeighingData::getOrderNo));

        List<WeighingData> pushList = new ArrayList<>();
        for (Map.Entry<String, List<WeighingData>> entry : groupMap.entrySet()) {
            WeighingData weighingData = dataMap.get(entry.getKey());
            if(weighingData == null){
                continue;
            }
            BigDecimal suttleTotal = BigDecimal.ZERO;
            BigDecimal tareTotal = BigDecimal.ZERO;
            BigDecimal grossTotal = BigDecimal.ZERO;
            for (WeighingData data : entry.getValue()) {
                if(data.getSuttle() != null){
                    suttleTotal = suttleTotal.add(data.getSuttle());
                    tareTotal = tareTotal.add(data.getTare());
                    grossTotal = grossTotal.add(data.getGross());
                }
            }
            BigDecimal thousand = new BigDecimal("1000");
            weighingData.setSuttle(suttleTotal.multiply(thousand));
            weighingData.setTare(tareTotal.multiply(thousand));
            weighingData.setGross(grossTotal.multiply(thousand));
            weighingData.setTotalWeight(weighingData.getTotalWeight().multiply(thousand));
            pushList.add(weighingData);
        }

        if(pushList.isEmpty()){
            return;
        }

        List<Long> successList = new ArrayList<>();
        for (WeighingData data : pushList) {
            BaseResult<String> result = HttpsRequest.pushData(dataPush, data);
            if("S".equals(result.getCode())){
                successList.add(data.getId());
            }
        }
        if(successList.isEmpty()){
            return;
        }
        //将推送成功的id列表的 isSend 改为 1已发送
        weighingDataService.updateBySuccessful(successList);
    }

    //地磅数据推送，将地磅数据、视频流数据通过id都查询出来，整合成一条后再推送
    public void wagonPushData(){
        //查询未推送的所有数据
        List<WagonDataVO> unpushedList = wagonDataService.getUnpushed();
        if(CollUtil.isEmpty(unpushedList)){
            return;
        }
        //将相同id的视频流数据查询出来，并组装完整的数据
        List<Long> idList = unpushedList.stream().map(WagonDataVO::getId).collect(Collectors.toList());
        List<ListVideoDataVO> listVideoList = listVideoDataService.getByIdList(idList);
        if(CollUtil.isEmpty(listVideoList)){
            return;
        }
        Map<Long, String> videoMap = listVideoList.stream()
                .collect(Collectors.toMap(ListVideoDataVO::getId, ListVideoDataVO::getListVideo, (v1, v2) -> v1));

        List<Long> successList = new ArrayList<>();
        for (WagonDataVO data : unpushedList) {
            //只有 wagondata 和 videodata 两张表都存在相同id的数据，也就是完整的数据，才会将数据整合后推送给大数据
            if(videoMap.containsKey(data.getId())){
                data.setListVideo(videoMap.get(data.getId()));
                BaseResult<String> result = HttpsRequest.wagonPushData(wagonDataPush, data);
                if("S".equals(result.getCode())){
                    successList.add(data.getId());
                }
            }
        }
        if(successList.isEmpty()){
            return;
        }
        //将推送成功的id列表的 isSend 改为 1已发送
        wagonDataService.updateBySuccessful(successList);
    }
}
