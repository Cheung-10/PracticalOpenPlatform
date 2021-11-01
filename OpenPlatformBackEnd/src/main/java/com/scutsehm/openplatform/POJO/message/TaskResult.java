package com.scutsehm.openplatform.POJO.message;

import com.scutsehm.openplatform.POJO.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@AllArgsConstructor
@ToString
public class TaskResult implements Serializable {
    /**
     * 任务名字
     */
    private String taskName;
    /**
     * 任务状态
     */
    private TaskStatus status;
}
