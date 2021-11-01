package com.scutsehm.openplatform.dao.repository;

import com.scutsehm.openplatform.POJO.entity.DeployTask;
import org.springframework.data.repository.CrudRepository;

public interface DeployTemplateRepository extends CrudRepository<DeployTask, String> {
}
