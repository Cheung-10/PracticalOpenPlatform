package com.scutsehm.openplatform.service.impl;

import com.scutsehm.openplatform.POJO.entity.DeployTemplate;
import com.scutsehm.openplatform.POJO.entity.TrainModelTemplate;
import com.scutsehm.openplatform.dao.repository.TrainModelTemplateRepository;
import com.scutsehm.openplatform.service.TrainModelTemplateService;
import com.scutsehm.openplatform.util.FileAndPathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(rollbackFor = Exception.class)
public class TrainModelTemplateServiceImpl implements TrainModelTemplateService {

    @Autowired
    private TrainModelTemplateRepository trainModelTemplateRepository;

    @Override
    public Long publish(TrainModelTemplate trainModelTemplate) {
        //checkPath
        FileAndPathUtils.validate(trainModelTemplate.getInputPathParameterTemplate().getDefaultValue());
        FileAndPathUtils.validate(trainModelTemplate.getOutputPathParameterTemplate().getDefaultValue());
        DeployTemplate deployTemplate = trainModelTemplate.getDeployTemplate();
        if(deployTemplate!=null){
            FileAndPathUtils.validate(deployTemplate.getInputPathParameterTemplate().getDefaultValue());
            FileAndPathUtils.validate(deployTemplate.getOutputPathParameterTemplate().getDefaultValue());
        }
        //用户空间下相对路径
        String applicationPath = trainModelTemplate.getRuntimeInformation().getApplicationPath();
        //绝对路径
        String fromPath = FileAndPathUtils.getAbsolutePathByPrivateSpace(applicationPath);
        //程序的相对路径2
        String trainModelPath = "/" +  FileAndPathUtils.getRandomName();
        //绝对路径
        String toPath = FileAndPathUtils.getAbsolutePathByTrainModelSpace(trainModelPath);

        //修正路径
        trainModelTemplate.getRuntimeInformation().setApplicationPath(trainModelPath);
        try {
            FileAndPathUtils.copy(new File(fromPath).toPath(), new File(toPath).toPath());
        } catch (IOException e) {
            throw new RuntimeException("复制文件失败",e);
        }
        //存储
        trainModelTemplateRepository.save(trainModelTemplate);
        return trainModelTemplate.getId();
    }

    @Override
    public List<TrainModelTemplate> getAll() {
        return (List<TrainModelTemplate>) trainModelTemplateRepository.findAll();
    }

    @Override
    public TrainModelTemplate getById(Long id) {
        Optional<TrainModelTemplate> result = trainModelTemplateRepository.findById(id);
        return result.orElse(null);
    }
}
