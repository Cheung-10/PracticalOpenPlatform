package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 运行环境下的模块信息，描述运行环境中可用的模块
 */
@Data
@Entity
public class Module {
    /**
     *  必须要有的id，一般用不上
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Null(message = "id不填")
    private Long id;

    /**
     * 模块名称
     */
    @NotNull(message = "模块名不能为空")
    @NotBlank(message = "模块名不能为空")
    private String name;

    /**
     * 版本号
     */
    @NotNull(message = "模块版本号不能为空")
    @NotBlank(message = "模块版本号不能为空")
    @Size(max = 255, message = "模块版本号不能超过255")
    private String version;
}
