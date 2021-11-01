package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 意义上也是运行参数，不同的是路径参数会有特殊处理
 * 因此特设此类
 */
@Data
@Embeddable
public class PathParameter implements Serializable {
    /**
     * 路径名字
     */
    @NotNull(message = "路径名不能为空")
    @NotBlank(message = "路径名不能为空")
    @Size(max = 255, message = "路径名字不能超过255")
    private String name;

    /**
     * 值
     */
    @NotNull(message = "路径值不能为空")
    @Valid
    private SpacePath value;
}
