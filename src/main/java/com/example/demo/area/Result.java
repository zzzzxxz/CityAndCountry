package com.example.demo.area;

import java.io.Serializable;

public class Result implements Serializable {

    private static final long serialVersionUID = -2062711552511675015L;
    private String code;

    private String msg;

    private Object data;

    public Result() {
    }

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    public static Result success(Object data) {
        Result result = new Result();
        result.setCode("1");
        result.setMsg("数据返回成功!");
        result.setData(data);
        return result;
    }


    public String getCode() {
        return this.code;
    }

    public Result setCode(final String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return this.msg;
    }

    public Result setMsg(final String msg) {
        this.msg = msg;
        return this;
    }


    public Object getData() {
        return this.data;
    }

    public Result setData(final Object data) {
        this.data = data;
        return this;
    }
}
