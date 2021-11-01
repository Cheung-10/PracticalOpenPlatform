package com.scutsehm.openplatform.dao.repository;

import com.scutsehm.openplatform.POJO.entity.TaskLog;
import com.scutsehm.openplatform.POJO.entity.TrainModelTask;
import com.scutsehm.openplatform.POJO.enums.TaskStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TaskLogRepository extends CrudRepository<TaskLog, String> {
}
