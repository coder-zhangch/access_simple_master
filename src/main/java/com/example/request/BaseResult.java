package com.example.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUId = 1L;

    private final static String successMsg = "操作成功！";
    private final static String failureMsg = "操作失败！";
    private final static String successCode = "E";
    private final static String failureCode = "F";

    /**
     * 例：
     * {
     *     "code": "S",
     *     "msg": "操作成功",
     *     "time": "2023-07-05 16:18:56",
     *     "data": null
     * }
     */

    //状态码 S:成功，F:失败 或者 0成功 -1失败
    private String code;

    //token
    private String token;

    //响应内容
    private String msg;

    //响应时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    private Date time;

    //未使用
    private T data;

    public static <T> BaseResult<T> ok(T data) {
        return restResult(data, successCode, successMsg);
    }

    public static <T> BaseResult<T> fail() {
        return restResult(null, failureCode, failureMsg);
    }

    public static <T> BaseResult<T> fail(String message) {
        return restResult(null, failureCode, message);
    }

    private static <T> BaseResult<T> restResult(T data, String code, String msg) {
        BaseResult<T> apiResult = new BaseResult<>();
        apiResult.setData(data);
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        apiResult.setTime(new Date());
        return apiResult;
    }
}
