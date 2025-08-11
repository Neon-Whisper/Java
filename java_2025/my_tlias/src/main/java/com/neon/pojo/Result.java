package com.neon.pojo;

import lombok.Data;

@Data
public class Result {
    private Integer code; //编码：1成功，0为失败
    private String msg; //错误信息
    private Object data; //数据

    public static Result success()
    {
        Result result = new Result();
        result.setCode(1);
        result.setMsg("success");
        return result;
    }

    public static Result success (Object object)
    {
        Result result = new Result();
        result.setCode(1);
        result.setMsg("success");
        result.setData(object);
        return result;
    }

    public static Result error (String msg)
    {
        Result result = new Result();
        result.setCode(0);
        result.setMsg(msg);
        return result;
    }
}
