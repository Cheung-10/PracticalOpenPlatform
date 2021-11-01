package com.scutsehm.openplatform.service.impl;

import com.scutsehm.openplatform.POJO.entity.RuntimeEnvironment;
import com.scutsehm.openplatform.dao.repository.RuntimeEnvironmentRepository;
import com.scutsehm.openplatform.service.RuntimeEnvironmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RuntimeEnvironmentServiceImpl implements RuntimeEnvironmentService {
    @Autowired
    private RuntimeEnvironmentRepository runtimeEnvironmentRepository;

    @Override
    public RuntimeEnvironment getById(Long id) {
        Optional<RuntimeEnvironment> optional = runtimeEnvironmentRepository.findById(id);
        return optional.orElse(null);

    }

    @Override
    public List<RuntimeEnvironment> getAll() {
        return (List<RuntimeEnvironment>) runtimeEnvironmentRepository.findAll();
    }

    @Override
    public Long add(RuntimeEnvironment runtimeEnvironment) {
        runtimeEnvironmentRepository.save(runtimeEnvironment);
        return  runtimeEnvironment.getId();
    }
}
