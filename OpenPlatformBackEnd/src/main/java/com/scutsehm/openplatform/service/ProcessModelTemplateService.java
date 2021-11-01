package com.scutsehm.openplatform.service;

import com.scutsehm.openplatform.POJO.entity.ProcessModelTemplate;

import java.util.List;

public interface ProcessModelTemplateService {
    Long publish(ProcessModelTemplate trainModelTemplate);
    List<ProcessModelTemplate> getAll();
    ProcessModelTemplate getById(Long id);
}
