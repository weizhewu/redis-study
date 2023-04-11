package com.example.redisstudy.common.result;


import java.io.Serializable;
import java.util.Objects;

/**
 * @author MrWei
 * @version 1.0.0
 * @date 2023/1/3 19:03
 * @description 统一返回格式
 */

public class ResponseResult<T> implements Serializable {
    private Integer code;

    private String msg;

    private T data;

    private ResponseResult() {
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ResponseResult<T> success() {
        ResponseResult<T> result = new ResponseResult<>();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    public static <T> ResponseResult<T> success(T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public static <T> ResponseResult<T> failure(ResultCode resultCode) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setResultCode(resultCode);
        return result;
    }

    public static <T> ResponseResult<T> failure(ResultCode resultCode, T data) {
        ResponseResult<T> result = new ResponseResult<>();
        result.setResultCode(resultCode);
        result.setData(data);
        return result;
    }

    public static <T> boolean ifSuccess(ResponseResult<T> result){
        if (Objects.isNull(result) || Objects.isNull(result.getCode())){
            return false;
        }
        return ResultCode.SUCCESS.code().equals(result.getCode());
    }

    public void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }
}
