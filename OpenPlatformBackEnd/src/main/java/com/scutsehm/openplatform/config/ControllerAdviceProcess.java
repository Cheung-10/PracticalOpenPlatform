package com.scutsehm.openplatform.config;

import com.scutsehm.openplatform.POJO.VO.Result;
import com.scutsehm.openplatform.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;


/**
 * 全局抓取异常向前端返回固定json格式的全局异常配置
 */
@ControllerAdvice
@Slf4j
public class ControllerAdviceProcess {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handleException(HttpServletRequest request, Exception exception){

        String code = "500";
        String msg = null;
        if (exception instanceof HttpMessageNotReadableException) {
            code = "400";
            msg = exception.getMessage();
            Result result = new Result(code, msg);
            return result;
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            code = "999";
            msg = exception.getMessage();
            Result result = new Result(code, msg);
            return result;
        }

        if(exception instanceof BusinessException){
            BusinessException bizException = (BusinessException) exception;
            msg = bizException.getMessage();
            code = bizException.getCode();
        }else{
            //出现了未定义的内部错误

            log.error(exception.getMessage(),exception);
            msg = exception.getMessage();
            code = "500";
        }

        Result result = new Result(code, msg);
        return result;
    }
}

