package com.scutsehm.openplatform.service;

import com.scutsehm.openplatform.POJO.entity.RuntimeEnvironment;

import java.util.List;

public interface RuntimeEnvironmentService {
    RuntimeEnvironment getById(Long id);
    List<RuntimeEnvironment> getAll();
    Long add(RuntimeEnvironment runtimeEnvironment);

}
