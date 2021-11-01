package com.scutsehm.openplatform.service;

import com.scutsehm.openplatform.POJO.entity.ProcessModelTask;

import java.util.List;

public interface ProcessModelTaskService {
    /**
     * 执行task
     * @param task 要执行的task
     * @return
     */
    String execute(ProcessModelTask task);

    /**
     * 停止执行任务
     * @param taskId 操作的task的id
     */
    void stop(String taskId);

    /**
     * 返回目前所有的日志
     * @param taskId 操作的task的id
     * @return 文本日志
     */
    String getLog(String taskId);

    /**
     * 返回task
     * @param taskId
     * @return
     */
    ProcessModelTask getById(String taskId);

    List<ProcessModelTask> getAllByCurrentUser();
}
