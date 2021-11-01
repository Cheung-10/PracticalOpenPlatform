package com.scutsehm.openplatform.exception.exceptionEnum;

import com.scutsehm.openplatform.exception.ErrorCode;

/**
 * 统一业务错误代码
 */
public enum BusinessErrorCode implements ErrorCode {
    UNSPECIFIED("500", "未知错误"),
    LOGINFAIL("401","登录失败，用户名或密码错误"),
    ACCESSFAIL("403","无权访问"),
    USEREXISTED("1001","用户名已存在"),
    JWTEXPIRED("1002","登录信息过时，请重新登录"),
    USERNAMEERROR("1003","注册的用户名不合法，请用学号或工号注册"),
    FILEEXISTED("1004","同名文件已存在"),
    FILEUNEXISTED("1005","文件不存在"),
    COPYDESTFILEEXISTED("1006","复制目标同名文件已存在"),
    COPYSOURCEFILEUNEXISTED("1007","复制源文件不存在"),
    UNZIPERROR("1008","无法对非ZIP文件进行解压缩"),
    NOACCESSTOFILESPACE("1009","当前操作不允许访问Data空间"),
    ;

    private String code;
    private String description;

    private BusinessErrorCode(String code, String description){
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

    /**
     * 根据code获得ErrorCode类实体
     * @param code
     * @return
     */
    public static ErrorCode getByCode(String code){
        for(BusinessErrorCode errorCode: BusinessErrorCode.values()){
            if(code.equals(errorCode.code))
                return errorCode;
        }
        return UNSPECIFIED;
    }
}
