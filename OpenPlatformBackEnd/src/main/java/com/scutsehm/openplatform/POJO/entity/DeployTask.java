package com.scutsehm.openplatform.POJO.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 自动部署的任务,把处理模型模板中有而部署模板没有的信息补全
 * 主要涉及name，description
 */
@Data
@Entity
public class DeployTask {

    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 部署的处理模型模板的名字
     */
    @NotNull(message = "名字不能为空")
    @NotBlank(message = "名字不能为空")
    @Size(max = 255, message = "名字不能超过255")
    private String name;

    /**
     * 部署的处理模型模板的描述
     */
    @NotNull(message = "描述不能为空")
    @NotBlank(message = "描述不能为空")
    @Size(max = 255, message = "描述不能超过255")
    private String description;

    /**
     * 处理模型的文件夹名字
     */
    private String processModelFolderPath;

    /**
     * 训练模型目标输出
     */
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="space", column = @Column(name = "target_output_space")),
            @AttributeOverride(name="path", column = @Column(name = "target_output_path"))})
    private SpacePath targetOutput;

//    /**
//     * 映射关系
//     */
//    @OneToOne(targetEntity = TrainModelTask.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    @JoinColumn(name = "train_model_task_id")
//    private TrainModelTask trainModelTask;
}
