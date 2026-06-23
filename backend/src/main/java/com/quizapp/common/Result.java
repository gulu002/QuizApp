package com.quizapp.common;

import lombok.Data;

/**
 * 统一响应结果类
 * <p>封装API响应的标准格式，包含状态码、消息和数据</p>
 *
 * @param <T> 数据类型
 * @author quizapp
 */
@Data
public class Result<T> {
    private int code;
    private String message;
    private T data;

    /**
     * 创建成功响应
     *
     * @param data 响应数据
     * @return 成功响应结果
     */
    public static <T> Result<T> success(T data) {
        Result<T> r = new Result<>();
        r.code = 200;
        r.message = "success";
        r.data = data;
        return r;
    }

    /**
     * 创建成功响应（无数据）
     *
     * @return 成功响应结果
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 创建错误响应
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 错误响应结果
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> r = new Result<>();
        r.code = code;
        r.message = message;
        return r;
    }

    /**
     * 创建错误响应（默认500错误码）
     *
     * @param message 错误信息
     * @return 错误响应结果
     */
    public static <T> Result<T> error(String message) {
        return error(500, message);
    }
}