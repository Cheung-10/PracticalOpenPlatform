package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * 部署模板，属性值相当于processTemplate中的template
 */
@Data
@Entity
public class DeployTemplate {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 应用程序（脚本）的程序入口
     */
    @Pattern(regexp ="^([^\\/]+)+$", message = "程序入口不合法")
    @NotNull(message = "程序入口不可为空")
    private String applicationEntry;

    /**
     * 运行参数
     */
    @OneToMany(targetEntity = RuntimeParameterTemplate.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "deploy_template_id")
    @Valid
    private List<RuntimeParameterTemplate> runtimeParameterTemplateList;

    /**
     * 输入路径参数
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="name", column=@Column(name = "input_path_name")),
            @AttributeOverride(name="defaultValue.space", column = @Column(name = "input_path_default_value_space")),
            @AttributeOverride(name="defaultValue.path", column = @Column(name = "input_path_default_value_path"))})
    @Valid
    @NotNull(message = "输入路径模板不可为空")
    private PathParameterTemplate inputPathParameterTemplate;

    /**
     * 输出路径参数
     */
    @Embedded
    @AttributeOverrides({@AttributeOverride(name="name", column=@Column(name = "output_path_name")),
            @AttributeOverride(name="defaultValue.space", column = @Column(name = "output_path_default_value_space")),
            @AttributeOverride(name="defaultValue.path", column = @Column(name = "output_path_default_value_path"))})
    @Valid
    @NotNull(message = "输出路径模板不可为空")
    private PathParameterTemplate outputPathParameterTemplate;
}
