package com.example.task;

import com.example.entity.SumData;
import com.example.request.BaseResult;
import com.example.request.HttpsRequest;

import java.util.concurrent.Callable;

/**
 * 推送数据
 * 如果推送失败，则返回推送的id，否则返回null
 */
public class DataPushCallable implements Callable<String> {

    private String message;
    private SumData sumData;

    public DataPushCallable(String message, SumData sumData){
        this.message = message;
        this.sumData = sumData;
    }

    @Override
    public String call() {
        try {
            BaseResult<String> result = HttpsRequest.pushData(message, sumData);
            if(result != null || "S".equals(result.getCode())){
                return null;
            }
            return sumData.getId();
        } catch (Exception e) {
            return sumData.getId();
        }
    }
}
