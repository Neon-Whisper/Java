package com.neon.exception;

import com.neon.pojo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class GlobalExceptionHandel {
    //处理异常
    @ExceptionHandler
    public Result ex(Exception e){//方法形参中指定能够处理的异常类型
        e.printStackTrace();//打印堆栈中的异常信息
        //捕获到异常之后，响应一个标准的Result
        return Result.error("对不起,操作失败,请联系管理员");
    }
}
