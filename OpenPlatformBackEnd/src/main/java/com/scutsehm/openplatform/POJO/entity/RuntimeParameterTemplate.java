package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;
import javax.persistence.Id;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 运行时参数，描述了参数的名字和类型，以及默认值
 */
@Data
@Entity
public class RuntimeParameterTemplate {

    /**
     *  必须要有的id，一般用不上
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Null(message = "id不填")
    private Long id;


    /**
     * 参数名字
     * 参数名字最后会被作为参数值前键值，--name value
     */
    @NotNull(message = "参数名字不能为空")
    @NotBlank(message = "参数名字不能为空")
    @Size(max = 255, message = "参数名字不能超过255")
    private String name;

    /**
     * 参数类型
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private Type type;


    /**
     * 参数默认值
     */
    @Size(max = 255, message = "参数默认值不能超过255")
    private String defaultValue;

    public static enum Type{
        INTEGER, DOUBLE, STRING, BOOLEAN
    }

    /**
     * 参数是否为必要参数
     */
    @NotNull(message = "必要性不能为空")
    private Boolean isNecessary;
}
