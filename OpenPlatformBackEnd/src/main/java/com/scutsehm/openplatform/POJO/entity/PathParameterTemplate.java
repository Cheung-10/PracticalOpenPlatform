package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 路径参数模板
 */
@Data
@Embeddable
public class PathParameterTemplate {
    /**
     * 路径参数名
     */
    @NotNull(message = "路径名不能为空")
    @NotBlank(message = "路径名不能为空")
    @Size(max = 255, message = "路径名字不能超过255")
    private String name;

    /**
     * 路径默认值
     */
    @NotNull(message = "路径默认值不能为空")
    @Valid
    private SpacePath defaultValue;
}
