package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;

public enum AutoProcessErrorCode implements ErrorCode {
    MODEL_CONFIG_GET_FAILED("430", "获取process模型的model_config（模型配置属性）失败")

    ;

    private String code;
    private String description;

    private AutoProcessErrorCode(String code, String description){
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
