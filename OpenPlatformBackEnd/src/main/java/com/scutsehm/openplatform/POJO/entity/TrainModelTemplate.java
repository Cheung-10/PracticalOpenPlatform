package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;
import lombok.ToString;

import javax.persistence.Id;


import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 训练模型的模板
 * 模板的属性分为两种，一种是不变的（即用户不能更改），一种是可变的（即给出提示让用户给出配置）
 */
@Data
@Entity
@ToString
public class TrainModelTemplate {
    /**
     * 运行模型模板id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Null(message = "id不填")
    private Long id;

    /**
     * 训练模板名字
     */
    @NotNull(message = "训练模板名字不能为空")
    @NotBlank(message = "训练模板名字不能为空")
    @Size(max = 255, message = "训练模板名字不能超过255")
    private String name;

    /**
     * 训练模板的描述信息
     */
    @NotNull(message = "描述不能为空")
    @NotBlank(message = "描述不能为空")
    @Size(max = 255, message = "描述不能超过255")
    private String description;

    /**
     * 运行信息
     */
    @Embedded
    @Valid
    @NotNull(message = "运行信息不可为空")
    private RuntimeInformation runtimeInformation;

    /**
     * 运行参数模板
     */
    @OneToMany(targetEntity = RuntimeParameterTemplate.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "train_model_template_id")
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


    /**
     * 自动部署配置
     */
    @Valid
    @OneToOne(targetEntity = DeployTemplate.class, cascade = CascadeType.ALL)
    private DeployTemplate deployTemplate;

    /**
     * 判单是否支持自动配置
     * @return
     */
    public boolean isSupportDeploy(){
        return deployTemplate!=null;
    }
}
