package com.scutsehm.openplatform.service;

import com.scutsehm.openplatform.POJO.entity.TrainModelTask;

import java.util.List;

public interface TrainModelTaskService {
    /**
     * 执行task
     * @param task 要执行的task
     * @return 任务id
     */
    String execute(TrainModelTask task);

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
    TrainModelTask getById(String taskId);

    /**
     * 获得当前用户的所有任务
     * @return
     */
    List<TrainModelTask> getAllByCurrentUser();
}
