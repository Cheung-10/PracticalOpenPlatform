package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;


/**
 * 11xx
 */
public enum TrainModelTaskErrorCode implements ErrorCode {
    NOT_SUPPORT_DEPLOY("1101", "Not Support Deploy"),
    RUNTIME_PARAMETER_ERROR("1102","Check the Values of Runtime Parameter");
    private final String code;
    private final String description;
    TrainModelTaskErrorCode(String code, String description){
        this.code = code;
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCode() {
        return code;
    }
}

