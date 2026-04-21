package com.example.task;

import cn.hutool.core.collection.CollUtil;
import com.example.entity.SumData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.SumDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    //缓存 最后推送的数据id 的key
    private final static String Scale1LastPushId = "Scale1LastPushId:";//秤号1
    private final static String Scale2LastPushId = "Scale2LastPushId:";//秤号2
    //缓存 未推送成功数据id列表 的key前缀
    private final static String failPushIdList = "FailPushIdList:";

    private final static String dataPush = "数据推送";
    private final static String failDataPush = "异常数据推送";
    private final static String comma = ",";

    @Autowired
    private SumDataService sumDataService;
    @Autowired
    private RedisTemplate redisTemplate;//每10秒钟执行一次


    @Async
//    @Scheduled(cron = "0 */1 * * * ? ")
    @Scheduled(cron = "0 */10 * * * ? ")
    public void push(){
        /*String time = "2024-09-01 00:00:00";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(time);
            Calendar calendar = Calendar.getInstance();
            if(calendar.getTime().getTime() >= date.getTime()){
                return;
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }*/
        //1号秤数据推送
        pushData("1", Scale1LastPushId);
        //暂停10秒
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }

        //2号秤数据推送
        pushData("2", Scale2LastPushId);

        //暂停10秒
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        //推送失败的数据重新推送
        repushData();
    }

    public void pushData(String scaleNo, String key){
        //如果redis中存在这个key，那么获取这个id，查询id后面的属于该秤号的数据，排除最后一条数据后，全部推送，最后一条数据是没有称重完成的
        if(redisTemplate.hasKey(key)){
            String lastId = redisTemplate.boundValueOps(key).get().toString();
            List<SumData> dataList = sumDataService.queryByLastId(scaleNo, lastId);
            if(CollUtil.isNotEmpty(dataList)){
                dataList.sort(Comparator.comparing(SumData::getId).reversed());
                /**
                 * 两种情况：
                 * 1、在数据库中，dataList之后还有非中粮贸易的id数据存在，则将dataList全部推送
                 * 2、在数据库中，dataList之后没有非中粮贸易的id数据存在，则排除dataList中的最后一个，其他的全部推送
                 */
                //查询该秤号下的最后一条数据
                SumData lastData = sumDataService.queryLastByScaleNo(scaleNo);
                //如果最后一条数据的id就是dataList中最大的id，那么需要排除掉这条数据
                if(dataList.get(0).getId().compareTo(lastData.getId()) == 0){
                    //删除id最大的一个，这条数据是没有称重完成的
                    dataList.remove(0);
                }
                if (dataList.size() > 0) {
                    List<String> failIdList = new ArrayList<>();
                    for (SumData sumData : dataList) {
                        BaseResult<String> result = HttpsRequest.pushData(dataPush, sumData);
                        if(result == null || !"S".equals(result.getCode())){
                            failIdList.add(sumData.getId());
                        }
                    }
                    if(CollUtil.isNotEmpty(failIdList)){
                        //重新赋值key，先获取redis中的推送失败的id
                        if(redisTemplate.hasKey(failPushIdList)){
                            String ids = redisTemplate.boundValueOps(failPushIdList).get().toString().trim();
                            failIdList.addAll(CollUtil.toList(ids.split(comma)));
                        }
                        String idJoin = failIdList.stream().distinct().collect(Collectors.joining(comma));
                        //3天过期
                        redisTemplate.boundValueOps(failPushIdList).set(idJoin, 3, TimeUnit.DAYS);
                    }
                    //获取最大的id，重新缓存到redis中
                    dataList.sort(Comparator.comparing(SumData::getId).reversed());
                    redisTemplate.boundValueOps(key).set(dataList.get(0).getId());
                }
            }
        }else{
            //否则，查询最后一条有效的数据，推送
            SumData sumData = sumDataService.getLastData(scaleNo);
            if(sumData == null){
                return;
            }
            BaseResult<String> result = HttpsRequest.pushData(dataPush, sumData);
            if(result == null || !"S".equals(result.getCode())){
                List<String> failIdList = new ArrayList<>();
                if(redisTemplate.hasKey(failPushIdList)){
                    String ids = redisTemplate.boundValueOps(failPushIdList).get().toString().trim();
                    failIdList.addAll(CollUtil.toList(ids.split(comma)));
                }
                failIdList.add(sumData.getId());
                String idJoin = failIdList.stream().distinct().collect(Collectors.joining(comma));
                //3天过期
                redisTemplate.boundValueOps(failPushIdList).set(idJoin, 3, TimeUnit.DAYS);
            }
            //更新redis，最后推送的id
            redisTemplate.boundValueOps(key).set(sumData.getId());
        }
    }

    public void repushData(){
        if(redisTemplate.hasKey(failPushIdList)){
            String ids = redisTemplate.boundValueOps(failPushIdList).get().toString().trim();
            List<String> idList = CollUtil.toList(ids.split(comma));
            List<SumData> dataList = sumDataService.queryByIdList(idList);
            if(CollUtil.isNotEmpty(dataList)){
                List<String> failIdList = new ArrayList<>();
                for (SumData sumData : dataList) {
                    BaseResult<String> result = HttpsRequest.pushData(failDataPush, sumData);
                    if(result == null || !"S".equals(result.getCode())){
                        failIdList.add(sumData.getId());
                    }
                }
                //先删除原有的 推送失败的id
                redisTemplate.delete(failPushIdList);
                if(CollUtil.isNotEmpty(failIdList)){
                    String idJoin = failIdList.stream().distinct().collect(Collectors.joining(comma));
                    //3天过期
                    redisTemplate.boundValueOps(failPushIdList).set(idJoin, 1, TimeUnit.DAYS);
                }
            }
        }
    }
}
