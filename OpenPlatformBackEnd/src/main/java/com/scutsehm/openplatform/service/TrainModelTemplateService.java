package com.scutsehm.openplatform.service;

import com.scutsehm.openplatform.POJO.entity.TrainModelTemplate;

import java.util.List;

public interface TrainModelTemplateService {
    Long publish(TrainModelTemplate trainModelTemplate);
    List<TrainModelTemplate> getAll();
    TrainModelTemplate getById(Long id);
}
