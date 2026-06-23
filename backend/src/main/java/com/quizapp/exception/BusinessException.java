package com.quizapp.exception;

import lombok.Getter;

/**
 * 业务异常类
 * <p>用于表示业务逻辑中的异常情况，包含错误码和错误信息</p>
 *
 * @author quizapp
 */
@Getter
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this(500, message);
    }
}