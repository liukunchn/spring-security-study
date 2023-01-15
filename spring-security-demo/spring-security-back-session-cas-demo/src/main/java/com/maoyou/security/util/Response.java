package com.maoyou.security.util;

public class Response<T> {

    private Integer code;

    private String message;

    private T data;

    private String redirectUrl;

    private String requestId;

    protected Response(ResponseCode responseCode, T data) {
        this.code = responseCode.code;
        this.message = responseCode.message;
        this.data = data;
    }

    protected Response(ResponseCode responseCode) {
        this(responseCode, (T) null);
    }

    protected Response(T data) {
        this(ResponseCode.SUCCESS, data);
    }

    protected Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Response(Integer code, String message) {
        this(code, message, null);
    }

    public Response(Integer code) {
        this(code, null);
    }

    public static <V> Response<V> success() {
        return new Response<>(ResponseCode.SUCCESS);
    }

    public static <V> Response<V> success(V data) {
        return new Response<>(ResponseCode.SUCCESS, data);
    }

    public static <V> Response<V> error() {
        return new Response<>(ResponseCode.SERVER_ERROR);
    }

    public static <V> Response<V> error(ResponseCode errorInfo) {
        return new Response<>(errorInfo);
    }

    public static <V> Response<V> error(Integer code) {
        return new Response<>(code);
    }

    public Response setData(T data) {
        this.data = data;
        return this;
    }

    public Response setMessage(String message) {
        this.message = message;
        return this;
    }

    public Response setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
