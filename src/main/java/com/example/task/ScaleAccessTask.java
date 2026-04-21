package com.example.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.example.entity.FailedData;
import com.example.entity.LatestData;
import com.example.entity.SumData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.FailedDataService;
import com.example.service.LatestDataService;
import com.example.service.SumDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 将最后推送的id和推送失败的id存储到Access数据库中，暂未实现
 */
@Component
@Slf4j
public class ScaleAccessTask {

    @Autowired
    private SumDataService sumDataService;
    @Autowired
    private LatestDataService latestDataService;
    @Autowired
    private FailedDataService failedDataService;

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static String dataPush = "数据推送";
    private final static String failDataPush = "异常数据推送";


    //每10秒钟执行一次
//    @Async
//    @Scheduled(cron = "*/10 * * * * ? ")
    public void run(){
//        pushData();
//        repushData();
        refreshData();
    }

    public void refreshData(){
        Boolean deleted = latestDataService.delete();
        System.out.println(deleted);
        deleted = failedDataService.delete();
        System.out.println(deleted);

        LatestData latestData = new LatestData();
        latestData.setId("171");
        latestData.setPushTime(new Date());
        Boolean inserted = latestDataService.insert(latestData);
        System.out.println(inserted);
    }

    public void pushData(){
        String scaleNo = "1";
        Date date = new Date();
        LatestData latestData = latestDataService.findOne();
        //如果没有id，则查询SumData中最后一条数据，推送
        if(latestData == null || StrUtil.isBlank(latestData.getId())){
            //否则，查询最后一条数据，推送
            SumData sumData = sumDataService.getLastData(scaleNo);
            if(sumData == null){
                return;
            }
            BaseResult<String> result = HttpsRequest.pushData(dataPush, sumData);
            if(result == null || !"S".equals(result.getCode())){
                FailedData failedData = new FailedData();
                failedData.setId(sumData.getId());
                failedData.setFirstPushTime(date);
                failedDataService.insert(Arrays.asList(failedData));
            }
            latestDataService.delete();
            LatestData newData = new LatestData();
            newData.setId(sumData.getId());
            newData.setPushTime(date);
            latestDataService.insert(newData);
            return;
        }
        List<SumData> dataList = sumDataService.queryByLastId(scaleNo, latestData.getId());
        if(CollUtil.isNotEmpty(dataList)){
            List<String> failIdList = new ArrayList<>();
            //批量推送，如果推送失败，则记录这个失败的id
            for (SumData sumData : dataList) {
                BaseResult<String> result = HttpsRequest.pushData(dataPush, sumData);
                if(result == null || !"S".equals(result.getCode())){
                    failIdList.add(sumData.getId());
                }
            }
            if(CollUtil.isNotEmpty(failIdList)){
                List<FailedData> failedList = new ArrayList<>();
                for (String id : failIdList) {
                    FailedData failedData = new FailedData();
                    failedData.setId(id);
                    failedData.setFirstPushTime(date);
                    failedList.add(failedData);
                }
                failedDataService.insert(failedList);
            }
            //获取最大的id，重新保存到access数据库中
            dataList.sort(Comparator.comparing(SumData::getId).reversed());
            latestDataService.delete();
            LatestData newData = new LatestData();
            newData.setId(dataList.get(0).getId());
            newData.setPushTime(date);
            latestDataService.insert(newData);
        }
    }

    //每10秒钟推送一次，将推送失败的数据重新推送
    public void repushData(){
        //三天前的时间
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        String time = format.format(calendar.getTime());
        List<FailedData> failedList = failedDataService.findByTime(time);
        if(CollUtil.isNotEmpty(failedList)){
            List<String> idList = failedList.stream().map(FailedData::getId).collect(Collectors.toList());
            List<SumData> dataList = sumDataService.queryByIdList(idList);
            if(CollUtil.isNotEmpty(dataList)){
                List<String> failIdList = new ArrayList<>();
                //批量推送，如果推送失败，则记录这个失败的id
                for (SumData sumData : dataList) {
                    BaseResult<String> result = HttpsRequest.pushData(failDataPush, sumData);
                    if(result == null || !"S".equals(result.getCode())){
                        failIdList.add(sumData.getId());
                    }
                }

                if(CollUtil.isEmpty(failIdList)){
                    //全部推送成功，删除所有数据
                    failedDataService.delete();
                }else {
                    //将 除推送失败之外 的数据全部删除
                    failedDataService.deleteNotIn(failIdList);
                }
            }
        }
    }
}
