package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;

public enum CommonBusinessErrorCode implements ErrorCode {
    NOT_FOUND("404","资源不存在");


    CommonBusinessErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    private String code;
    private String description;
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCode() {
        return code;
    }
}
