package com.scutsehm.openplatform.POJO.enums;

import java.io.Serializable;

/**
 * 文件空间的枚举类
 */
public enum FileSpace implements Serializable {
    PRIVATE,    //用户空间
    SHARE,      //共享空间
    DATA,       //数据空间
    TRAINMODEL,      //训练模型空间
    PROCESSMODEL,    //处理模型空间

    NONE;       //不存在，当不需要文件路径时（如没有输入文件路径）时作标识使用，当然也无法被解析路径
}
