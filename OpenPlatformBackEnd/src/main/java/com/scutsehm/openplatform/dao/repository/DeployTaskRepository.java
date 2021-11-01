package com.scutsehm.openplatform.dao.repository;

import com.scutsehm.openplatform.POJO.entity.DeployTask;
import com.scutsehm.openplatform.POJO.entity.TaskLog;
import org.springframework.data.repository.CrudRepository;

public interface DeployTaskRepository extends CrudRepository<DeployTask, Long> {
//    DeployTask findDeployTaskByTrainModelTaskId(String tranModelTaskId);
}
