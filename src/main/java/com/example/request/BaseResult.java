package com.example.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseResult<T> implements Serializable {

    private static final long serialVersionUId = 1L;

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
}
