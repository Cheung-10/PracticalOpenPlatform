package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 运行参数键值对
 */
@Data
@Entity
public class RuntimeParameter {
    /**
     *  必须要有的id，一般用不上
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Null(message = "id不填")
    private Long id;

    /**
     * 参数名字
     */
    @NotNull(message = "参数名字不能为空")
    @NotBlank(message = "参数名字不能为空")
    @Size(max = 255, message = "参数名字不能超过255")
    private String name;

    /**
     * 参数默认值
     */
    @Size(max = 255, message = "参数值不能超过255")
    private String value;
}
