package com.doudoudrive.util.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>状态返回类，数据返回响应的封装</p>
 * <p>通用面向对象基础返回数据封装</p>
 * 2019-10-30 02:00
 *
 * @author Dan
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 描述
     */
    private String message;

    /**
     * 对象
     */
    private T data;

    private Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result(Integer code, String message, T obj) {
        this.code = code;
        this.message = message;
        this.data = obj;
    }

    public static <T> Result<T> build(Integer code, String message, T data) {
        return new Result<>(code, message, data);
    }

    public Integer getCode() {
        return code;
    }

    public Result<T> code(Integer code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Result<T> message(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> data(T data) {
        this.data = data;
        return this;
    }
}