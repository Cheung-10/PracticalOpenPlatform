package com.scutsehm.openplatform.POJO.entity;

import com.scutsehm.openplatform.POJO.enums.TaskStatus;
import lombok.Data;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 处理模型默认
 */
@Entity
@Data
public class ProcessModelTask {

    /**
     * 处理任务的id
     */
    @Id
    @Size(max = 255, message = "id不能超过255")
    private String id;

    /**
     * 调度方式
     */
    @NotNull
    private DispatchMode dispatchMode;

    public enum DispatchMode {
        SHELL, KUBERNETES
    }

    /**
     * 任务状态
     */
    private TaskStatus taskStatus;


    /**
     * 任务发布者
     */
    private String publisher;


    /**
     * 处理模板的id
     */
    @NotNull
    private Long processModelTemplateId;

    /**
     * 输入路径
     */
    @Embedded
    @Valid
    @AttributeOverrides({@AttributeOverride(name = "name", column = @Column(name = "input_path_parameter_name")),
            @AttributeOverride(name = "value.path", column = @Column(name = "input_path_parameter_value_path")),
            @AttributeOverride(name = "value.space", column = @Column(name = "input_path_parameter_value_space"))})
    @NotNull
    private PathParameter inputPathParameter;

    /**
     * 输出路径
     */
    @Embedded
    @Valid
    @AttributeOverrides({@AttributeOverride(name = "name", column = @Column(name = "output_path_parameter_name")),
            @AttributeOverride(name = "value.path", column = @Column(name = "output_path_parameter_value_path")),
            @AttributeOverride(name = "value.space", column = @Column(name = "output_path_parameter_value_space"))})
    @NotNull
    private PathParameter outputPathParameter;

    /**
     * 程序参数
     */
    @OneToMany(targetEntity = RuntimeParameter.class, cascade = CascadeType.ALL)
    @JoinColumn(name = "process_model_task_id")
    private List<RuntimeParameter> runtimeParameterList;

}
