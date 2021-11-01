package com.scutsehm.openplatform.POJO.entity;

import com.scutsehm.openplatform.POJO.enums.FileSpace;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * 空间下的路径
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class SpacePath implements Serializable {

    /**
     * 文件空间
     */
    @NotNull
    private FileSpace space;

    /**
     * 路径
     * FIXME 应检查传入的路径，避免访问上级文件夹，目前该检查由FileAndPathUtils.Validate方法实现
     */
    @NotNull(message = "路径名不能为空")
    @Pattern(regexp ="^\\/([^\\/]+\\/?)*$", message = "路径不合法")
    private String path;
}
