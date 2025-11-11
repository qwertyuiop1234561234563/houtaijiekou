package com.houtai.entity;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;
    private Boolean success;

    public static  ApiResponse success( ) {
        ApiResponse response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("成功");
        response.setSuccess(true);
        return response;
    }

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("成功");
        response.setData(data);
        response.setSuccess(true);
        return response;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(500);
        response.setMessage(message);
        response.setSuccess(false);
        return response;
    }
}