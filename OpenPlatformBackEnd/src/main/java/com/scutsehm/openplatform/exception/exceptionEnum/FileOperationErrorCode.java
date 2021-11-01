package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;

public enum FileOperationErrorCode implements ErrorCode {
    PROCESS_MODEL_LIST_WRITE_FAILED("490", "将processModelList写入配置文件时错误"),
    TRAIN_MODEL_LIST_WRITE_FAILED( "490", "将trainModelList写入配置文件时错误")
    ;

    private String code;
    private String description;

    private FileOperationErrorCode(String code, String description){
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
