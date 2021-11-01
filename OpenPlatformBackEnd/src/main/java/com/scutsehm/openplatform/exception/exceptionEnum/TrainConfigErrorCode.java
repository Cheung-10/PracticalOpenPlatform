package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;

/**
 * 操作TrainConfig时的错误代码
 */
public enum TrainConfigErrorCode implements ErrorCode {
    TRAIN_PROFILE_ILLEGAL("410", "Train模型配置文件异常"),
    TRAIN_CONFIG_OPTION_TYPE_ILLEGAL("410", "trainConfig属性项类型非法"),
    REQUIRED_OPTION_ILLEGAL("410", "trainConfig必要项非法")


    ;
    private String code;
    private String description;

    private TrainConfigErrorCode(String code, String description){
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
