package com.example.task;

import cn.hutool.core.collection.CollUtil;
import com.example.entity.LqData;
import com.example.entity.WeighingData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;
import com.example.service.LqDataService;
import com.example.service.WeighingDataService;
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

    private final static String LAST_PUSH_DATE = "LAST_PUSH_DATE:";

    //缓存 最后推送的数据id 的key
    private final static String ScaleLastPushId = "ScaleLastPushId:";
    //缓存 未推送成功数据id列表 的key前缀
    private final static String failPushIdList = "FailPushIdList:";
    //缓存 最后五个有结束标识的id
    private final static String lastFiveIdList = "LastFiveIdList:";
    //缓存 LqData最后推送的数据id 的key
    private final static String LqData1_2LastPushId = "LqData1-2LastPushId:";
    private final static String LqData2_2LastPushId = "LqData2-2LastPushId:";
    private final static String LqData3_2LastPushId = "LqData3-2LastPushId:";

    private final static String dataPush = "数据推送: ";
    private final static String failDataPush = "失败数据重推: ";
    private final static String dataRepush = "数据重新推送: ";
    private final static String comma = ",";

    private final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Autowired
    private WeighingDataService weighingDataService;
    @Autowired
    private RedisTemplate redisTemplate;//每10秒钟执行一次
    @Autowired
    private LqDataService lqDataService;


    @Async
//    @Scheduled(cron = "0 */1 * * * ? ")
    @Scheduled(cron = "0 */5 * * * ? ")
    public void push(){
//        String time = "2024-12-31 00:00:00";
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        try {
//            Date date = format.parse(time);
//            Calendar calendar = Calendar.getInstance();
//            if(calendar.getTime().getTime() >= date.getTime()){
//                return;
//            }
//        } catch (ParseException e) {
//            throw new RuntimeException(e);
//        }
        //检查是否存在中途修改了结束标识的数据
        queryLastFiveIdList();
        //暂停10秒
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        //数据推送
        pushData();
        //暂停10秒
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
        }
        //推送失败的数据重新推送
        repushData();
    }

    //每天12点执行一次
    @Async
    @Scheduled(cron = "0 0 12 * * ? ")
    public void pushLqDataJob(){
        pushLqData();
    }

    public void pushData(){
        //如果redis中存在这个key，那么获取这个id，查询id后面的属于该秤号的数据，排除最后一条数据后，全部推送，最后一条数据是没有称重完成的
        if(redisTemplate.hasKey(ScaleLastPushId)){
            String lastId = redisTemplate.boundValueOps(ScaleLastPushId).get().toString();
            List<WeighingData> dataList = weighingDataService.queryByLastId(lastId);
            if(CollUtil.isNotEmpty(dataList)){
                List<String> failIdList = new ArrayList<>();
                for (WeighingData data : dataList) {
                    BaseResult<String> result = HttpsRequest.pushData(dataPush, data);
                    if(result == null || !"S".equals(result.getCode())){
                        failIdList.add(String.valueOf(data.getEntryID()));
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
                dataList.sort(Comparator.comparing(WeighingData::getEntryID).reversed());
                String firstId = dataList.get(0).getEntryID().toString();
                redisTemplate.boundValueOps(ScaleLastPushId).set(firstId);

                //查询最新的5条有结束标识的数据，重新缓存到redis中
                List<WeighingData> lastFiveList = weighingDataService.queryLastFive(firstId);
                if(CollUtil.isEmpty(lastFiveList)){
                    return;
                }
                String newLastFiveId = lastFiveList.stream()
                        .map(item -> item.getEntryID().toString())
                        .collect(Collectors.joining(comma));
                redisTemplate.boundValueOps(lastFiveIdList).set(newLastFiveId);
            }
        }else{
            //否则，查询最后一条有效的数据，推送
            WeighingData lastData = weighingDataService.getLastData();
            if(lastData == null){
                return;
            }
            BaseResult<String> result = HttpsRequest.pushData(dataPush, lastData);
            if(result == null || !"S".equals(result.getCode())){
                List<String> failIdList = new ArrayList<>();
                if(redisTemplate.hasKey(failPushIdList)){
                    String ids = redisTemplate.boundValueOps(failPushIdList).get().toString().trim();
                    failIdList.addAll(CollUtil.toList(ids.split(comma)));
                }
                failIdList.add(String.valueOf(lastData.getEntryID()));
                String idJoin = failIdList.stream().distinct().collect(Collectors.joining(comma));
                //3天过期
                redisTemplate.boundValueOps(failPushIdList).set(idJoin, 3, TimeUnit.DAYS);
            }
            //更新redis，最后推送的id
            redisTemplate.boundValueOps(ScaleLastPushId).set(lastData.getEntryID());
        }
    }

    public void repushData(){
        if(redisTemplate.hasKey(failPushIdList)){
            String ids = redisTemplate.boundValueOps(failPushIdList).get().toString().trim();
            List<String> idList = CollUtil.toList(ids.split(comma));
            List<WeighingData> dataList = weighingDataService.queryByIdList(idList);
            if(CollUtil.isNotEmpty(dataList)){
                List<String> failIdList = new ArrayList<>();
                for (WeighingData weighingData : dataList) {
                    BaseResult<String> result = HttpsRequest.pushData(failDataPush, weighingData);
                    if(result == null || !"S".equals(result.getCode())){
                        failIdList.add(String.valueOf(weighingData.getEntryID()));
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

    //查询最后五条数据，是否存在修改了结束标识的
    //例：redis中：1,3,5,7,9   数据库中：3,4,5,7,9  多了4这个id，所以需要将4重新推送一次
    public void queryLastFiveIdList(){
        String lastId = null;
        if(redisTemplate.hasKey(ScaleLastPushId)){
            lastId = redisTemplate.boundValueOps(ScaleLastPushId).get().toString();
        }
        //查询数据库获取最后5条有结束标识的数据
        List<WeighingData> lastFiveList = weighingDataService.queryLastFive(lastId);
        if(CollUtil.isEmpty(lastFiveList)){
            return;
        }
        String newLastFiveId = lastFiveList.stream()
                .map(item -> item.getEntryID().toString())
                .collect(Collectors.joining(comma));

        //从redis中拿到 lastFiveIdList 对应的value，就是redis存储的最后五条id
        if(redisTemplate.hasKey(lastFiveIdList)){
            Map<String, WeighingData> newLastFiveIdMap = lastFiveList.stream()
                    .collect(Collectors.toMap(item -> item.getEntryID().toString(), Function.identity(), (v1, v2) -> v1));
            String oldLastFiveId = redisTemplate.boundValueOps(lastFiveIdList).get().toString();
            if(newLastFiveId.equals(oldLastFiveId)){
                return;
            }
            List<String> oldLastFiveIdList = Arrays.asList(oldLastFiveId.split(comma));

            List<WeighingData> pushList = new ArrayList<>();
            for (Map.Entry<String, WeighingData> entry : newLastFiveIdMap.entrySet()) {
                if(oldLastFiveIdList.contains(entry.getKey())){
                    continue;
                }
                pushList.add(entry.getValue());
            }

            List<String> failIdList = new ArrayList<>();
            if(!pushList.isEmpty()){
                for (WeighingData weighingData : pushList) {
                    BaseResult<String> result = HttpsRequest.pushData(dataRepush, weighingData);
                    if(result == null || !"S".equals(result.getCode())){
                        failIdList.add(String.valueOf(weighingData.getEntryID()));
                    }
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

            //更新redis
            redisTemplate.boundValueOps(lastFiveIdList).set(newLastFiveId);
        }else {
            //将最后五条的id放到redis中
            redisTemplate.boundValueOps(lastFiveIdList).set(newLastFiveId);
        }
    }

    public void pushLqData(){
        LqData lq1_2 = lqDataService.getLastBy("1-2");
        LqData lq2_2 = lqDataService.getLastBy("2-2");
        LqData lq3_2 = lqDataService.getLastBy("3-2");
        List<LqData> pushList = new ArrayList<>();
        pushList.add(lq1_2);
        pushList.add(lq2_2);
        pushList.add(lq3_2);
        if(pushList.isEmpty()){
            return;
        }

        String currentDate = dateFormat.format(new Date());
        for (LqData lqData : pushList) {
            if (lqData == null) {
                continue;
            }
            //不推送的情况：数据的日期和今天不是同一天则不推送
            String date = dateFormat.format(lqData.getJcsj());
            if(!currentDate.equals(date)){
                log.info("id--{}--{}仓--因非今天采集数据取消推送，今天日期为：{}，数据日期为：{}！", lqData.getId(), lqData.getCfmc(), currentDate, date);
                continue;
            }
            //redis中已存在今天的日期则不推送
            String key = LAST_PUSH_DATE + lqData.getCfmc();
            if(redisTemplate.hasKey(key)){
                String cacheDate = redisTemplate.boundValueOps(key).get().toString();
                if(currentDate.equals(cacheDate)){
                    log.info("id=={}=={}仓==因redis中已缓存当天日期取消推送，数据日期为：{}，缓存日期为：{}！", lqData.getId(), lqData.getCfmc(), date, cacheDate);
                    continue;
                }
            }
            try {
                lqDataService.dataPush(lqData);
                log.info("3推送成功: {}", lqData.getId());
            } catch (Exception e) {
                log.error(e.getMessage());
//                e.printStackTrace();
                log.info("4推送失败: {}", lqData.getId());
            }
            redisTemplate.boundValueOps(key).set(currentDate);
        }
    }
}
