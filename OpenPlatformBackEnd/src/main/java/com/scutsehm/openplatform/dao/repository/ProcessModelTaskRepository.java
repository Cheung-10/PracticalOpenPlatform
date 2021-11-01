package com.scutsehm.openplatform.dao.repository;

import com.scutsehm.openplatform.POJO.entity.ProcessModelTask;
import com.scutsehm.openplatform.POJO.entity.TrainModelTask;
import com.scutsehm.openplatform.POJO.enums.TaskStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProcessModelTaskRepository extends CrudRepository<ProcessModelTask, String> {
    @Modifying
    @Query("UPDATE ProcessModelTask SET taskStatus = ?1 WHERE id = ?2")
    void updateTaskStatusById(TaskStatus status, String jobId);

    List<ProcessModelTask> findAllByPublisher(String currentUser);

    @Query("SELECT publisher FROM ProcessModelTask WHERE id=?1")
    String findPublisherById(String taskId);
}
