package com.maoyou.security.util;

import lombok.Getter;

@Getter
public enum ResponseCode {
    /**
     * 成功
     */
    SUCCESS(0, "成功"),
    /**
     * 系统异常
     */
    SERVER_ERROR(99999, "系统异常"),

    /**==================业务异常===================**/

    // 未登录（可能的值有未登录，您的账号在异地登录，session过期？）（这里偷懒了，应该列出具体子类）
    UNAUTHENTICATE(10101, null),

    // 未授权（可能的值有未登录，您的账号在异地登录，session过期？）（这里偷懒了，应该列出具体子类）
    UNAUTHORIZED(10102, null),

    // 登录异常（具体异常为登录失败处理器中的AuthenticationException子类型）（这里偷懒了，应该列出具体子类）
    AUTHENTICATE_ERROR(10103, null),

    ;

    Integer code;
    String message;

    ResponseCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
