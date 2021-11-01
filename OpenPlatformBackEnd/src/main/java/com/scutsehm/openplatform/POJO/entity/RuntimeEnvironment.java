package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;

import javax.persistence.*;


import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 脚本程序的运行环境，如pytorch镜像
 */
@Data
@Entity
public class RuntimeEnvironment {

    /**
     * 运行环境id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Null(message = "id不填")
    private Long id;

    /**
     * 运行环境名称
     */
    @NotNull(message = "运行环境名称不能为空")
    @NotBlank(message = "运行环境名称不能为空")
    @Size(max = 255, message = "运行环境名称不能超过255")
    private String name;

    /**
     * 描述信息
     */
    @NotNull(message = "运行环境描述信息不能为空")
    @NotBlank(message = "运行环境描述信息不能为空")
    @Size(max = 255, message = "运行环境描述信息不能超过255")
    private String description;

    /**
     * 模块列表
     */
    @OneToMany(targetEntity = Module.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "runtime_environment_id")
    @Valid
    private List<Module> moduleList;
}
