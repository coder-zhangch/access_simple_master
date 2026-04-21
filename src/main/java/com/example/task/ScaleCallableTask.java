package com.example.task;

import com.example.entity.SumData;
import com.example.entity.User;
import com.example.service.SumDataService;
import com.example.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import cn.hutool.core.collection.CollUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 通过多线程的方式推送数据，测试有问题，停用
 */
@Component
@Slf4j
public class ScaleCallableTask {

    @Autowired
    private UserService userService;

//    @Scheduled(cron = "*/3 * * * * ? ")
    public void writeData(){
        //每3秒写一条数据
        ExecutorService executorService= Executors.newCachedThreadPool();
        executorService.submit(() -> {
            long time = System.currentTimeMillis();
            User user = new User();
            user.setUsername(String.valueOf(time));
            Boolean saved = userService.save(user);
            log.info("---------------------------保存成功{}", saved);
        });
    }

    //缓存 最后推送的数据id 的key
    private final static String lastPushId = "LastPushId";
    //缓存 未推送成功数据id列表 的key前缀
    private final static String failPushIdList = "FailPushIdList";

    private final static String dataPush = "数据推送";
    private final static String failDataPush = "异常数据推送";

    @Autowired
    private SumDataService sumDataService;
    @Autowired
    private RedisTemplate redisTemplate;

    //每10秒钟执行一次
//    @Async
//    @Scheduled(cron = "*/10 * * * * ? ")
    public void pushData(){
        String scaleNo = "1";
        ExecutorService pool = Executors.newCachedThreadPool();
        //如果redis中存在这个key，那么获取这个id，查询id后面的数据，全部推送
        if(redisTemplate.hasKey(lastPushId)){
            String lastId = redisTemplate.boundValueOps(lastPushId).get().toString();
            List<SumData> dataList = sumDataService.queryByLastId(scaleNo, lastId);
            if(CollUtil.isNotEmpty(dataList)){
                //线程池中批量推送数据
                List<Future<String>> futures = new ArrayList<>();
                for (SumData sumData : dataList) {
                    futures.add(pool.submit(new DataPushCallable(dataPush, sumData)));
                }
                pool.shutdown();
                //返回的是推送失败的id，保存到redis中，后面需要重新推送
                List<String> failIdList = new ArrayList<>();
                for (Future<String> future : futures) {
                    try {
                        if(future != null && future.get() != null){
                            failIdList.add(future.get());
                        }
                    } catch (Exception e) {
                    }
                }
                if(CollUtil.isNotEmpty(failIdList)){
                    for (String id : failIdList) {
                        //3天过期
                        redisTemplate.boundValueOps(failPushIdList + id).set(id, 3, TimeUnit.DAYS);
                    }
                }
                //获取最大的id，重新缓存到redis中
                dataList.sort(Comparator.comparing(SumData::getId).reversed());
                log.info("========b缓存最大的id========={}", dataList.get(0).getId());
//                redisTemplate.boundValueOps(lastPushId).set(dataList.get(0).getId());
            }
        }else{
            //否则，查询最后一条数据，推送
            SumData sumData = sumDataService.getLastData(scaleNo);
            if(sumData == null){
                return;
            }
            Future<String> future = pool.submit(new DataPushCallable(dataPush, sumData));
            pool.shutdown();
            try {
                if(future != null && future.get() != null){
                    String id = future.get();
                    //3天过期
                    redisTemplate.boundValueOps(failPushIdList + id).set(id, 3, TimeUnit.DAYS);
                }
                //更新redis，最后推送的id
                log.info("========a缓存最大的id========={}", sumData.getId());
//                redisTemplate.boundValueOps(lastPushId).set(sumData.getId());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        pool.shutdown();
    }

    //每1分钟推送一次，将推送失败的数据重新推送
//    @Async
//    @Scheduled(cron = "*/10 * * * * ? ")
    public void repushData(){
        String keys = "*" + failPushIdList + "*";
        Boolean b = redisTemplate.hasKey(failPushIdList + "195");
        System.out.println(b);
        Set<String> keySet = redisTemplate.keys(keys);
        if(CollUtil.isNotEmpty(keySet)){
            List<String> idList = keySet.stream()
                    .map(item -> item.replace(failPushIdList, "").trim())
                    .collect(Collectors.toList());
            List<SumData> dataList = sumDataService.queryByIdList(idList);
            if(CollUtil.isNotEmpty(dataList)){
                //线程池中批量推送数据
                ExecutorService pool = Executors.newCachedThreadPool();
                List<Future<String>> futures = new ArrayList<>();
                for (SumData sumData : dataList) {
                    futures.add(pool.submit(new DataPushCallable(failDataPush, sumData)));
                }
                pool.shutdown();
                //返回的是推送失败的id，保存到redis中，后面需要重新推送
                List<String> failIdList = new ArrayList<>();
                for (Future<String> future : futures) {
                    try {
                        if(future != null && future.get() != null){
                            failIdList.add(future.get());
                        }
                    } catch (Exception e) {
                    }
                }
                if(CollUtil.isNotEmpty(failIdList)){
                    //先删除原有的 推送失败的id
                    redisTemplate.delete(failPushIdList + "*");
                    for (String id : failIdList) {
                        //3天过期
                        redisTemplate.boundValueOps(failPushIdList + id).set(id, 3, TimeUnit.DAYS);
                    }
                }
            }
        }
    }
}
