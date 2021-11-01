package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@Embeddable
public class RuntimeInformation {
    /**
     * 应用程序（脚本）的路径
     */
    @Pattern(regexp ="^\\/([^\\/]+\\/?)+$", message = "路径不合法")
    //TODO 路径长度还没做限制
    private String applicationPath;

    /**
     * 应用程序（脚本）的程序入口
     */
    @Pattern(regexp ="^([^\\/]+)+$", message = "程序入口不合法")
    private String applicationEntry;

    /**
     * 运行环境的id
     */
    @NotNull(message = "运行环境id不能为空")
    private Long runtimeEnvironmentId;
}
