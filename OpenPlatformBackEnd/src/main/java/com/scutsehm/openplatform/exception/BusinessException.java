package com.scutsehm.openplatform.exception;

import com.scutsehm.openplatform.exception.exceptionEnum.BusinessErrorCode;

/**
 * 通用的业务Exception类
 */
public class BusinessException extends RuntimeException{
    private ErrorCode errorCode;

    public String getCode(){
        return errorCode.getCode();
    }

    public String getDescription(){
        return errorCode.getDescription();
    }

    /**
     * 无参，默认为UNSPECIFIED
     */
    public BusinessException(){
        super(BusinessErrorCode.UNSPECIFIED.getDescription());
        this.errorCode = BusinessErrorCode.UNSPECIFIED;
    }

    /**
     * 指定ErrorCode构造异常
     */
    public BusinessException(ErrorCode errorCode){
        super(errorCode.getDescription());
        this.errorCode = errorCode;
    }

    /**
     * 指定Throwable类构造异常
     */
    public BusinessException(Throwable t){
        super(t);
        this.errorCode = BusinessErrorCode.UNSPECIFIED;
    }

    /**
     * 自定义异常描述
     */
    public BusinessException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }
}
