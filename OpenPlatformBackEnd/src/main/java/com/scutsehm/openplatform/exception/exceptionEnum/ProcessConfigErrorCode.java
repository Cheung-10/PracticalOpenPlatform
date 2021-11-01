package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;

public enum ProcessConfigErrorCode implements ErrorCode{
    PROCESS_PROFILE_ILLEGAL("420", "process模型配置文件异常"),
    PROCESS_CONFIG_OPTION_TYPE_ILLEGAL("420", "processConfig属性项类型非法"),
    ;

    private String code;
    private String description;

    private ProcessConfigErrorCode(String code, String description){
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
