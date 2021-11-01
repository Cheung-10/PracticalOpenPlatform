package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;

public enum AutoTrainErrorCode implements ErrorCode {
    MODEL_CONFIG_GET_FAILED("440", "获取train模型的model_config（模型配置属性）失败"),
    DEPLOY_PROFILE_WRITE_FAILED("441", "自动部署时写入config.ini失败"),
    DEPLOY_GET_RANDOM_FILE_FAILED("442", "自动部署时获取随机输出文件夹失败"),
    DEPLOY_COPY_FAILED("443", "自动部署时拷贝文件到输出文件夹失败")
    ;

    private String code;
    private String description;

    private AutoTrainErrorCode(String code, String description){
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
