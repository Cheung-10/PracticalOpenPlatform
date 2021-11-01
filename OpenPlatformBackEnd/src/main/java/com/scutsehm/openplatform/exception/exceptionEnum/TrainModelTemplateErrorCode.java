package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;


public enum TrainModelTemplateErrorCode implements ErrorCode {
    NOT_FOUND("404", "Resource Not Found");

    private final String code;
    private final String description;
    TrainModelTemplateErrorCode(String code, String description){
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

