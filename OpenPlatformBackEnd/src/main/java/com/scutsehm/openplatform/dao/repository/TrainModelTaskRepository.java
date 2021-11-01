package com.scutsehm.openplatform.dao.repository;

import com.scutsehm.openplatform.POJO.entity.DeployTask;
import com.scutsehm.openplatform.POJO.entity.TrainModelTask;
import com.scutsehm.openplatform.POJO.enums.TaskStatus;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TrainModelTaskRepository extends CrudRepository<TrainModelTask, String> {
    @Modifying
    @Query("UPDATE TrainModelTask SET taskStatus = ?1 WHERE id = ?2")
    void updateTaskStatusById(TaskStatus status, String jobId);

    TrainModelTask findTrainModelTaskByDeployTaskId(Long deployTaskId);

    @Query("SELECT t.deployTask.id FROM TrainModelTask t WHERE t.id =?1")
    Long findDeployTaskIdById(String id);

    List<TrainModelTask> findAllByPublisher(String publisher);

    @Query("SELECT publisher FROM TrainModelTask WHERE id=?1")
    String findPublisherById(String taskName);
}
