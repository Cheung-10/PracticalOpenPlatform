package com.scutsehm.openplatform.service.impl;

import com.scutsehm.openplatform.POJO.entity.ProcessModelTemplate;
import com.scutsehm.openplatform.dao.repository.ProcessModelTemplateRepository;
import com.scutsehm.openplatform.service.ProcessModelTemplateService;
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
public class ProcessModelTemplateServiceImpl implements ProcessModelTemplateService {

    @Autowired
    private ProcessModelTemplateRepository processModelTemplateRepository;

    @Override
    public Long publish(ProcessModelTemplate processModelTemplate) {
        //checkPath
        FileAndPathUtils.validate(processModelTemplate.getInputPathParameterTemplate().getDefaultValue());
        FileAndPathUtils.validate(processModelTemplate.getOutputPathParameterTemplate().getDefaultValue());
        //用户空间下路径
        String applicationPath = processModelTemplate.getRuntimeInformation().getApplicationPath();
        //绝对路径
        String fromPath = FileAndPathUtils.getAbsolutePathByPrivateSpace(applicationPath);
        //程序的相对路径2
        String programPath = "/" +  FileAndPathUtils.getRandomName();
        //绝对路径
        String toPath = FileAndPathUtils.getAbsolutePathByProcessModelSpace(programPath);

        //修正路径
        processModelTemplate.getRuntimeInformation().setApplicationPath(programPath);
        try {
            FileAndPathUtils.copy(new File(fromPath).toPath(), new File(toPath).toPath());
        } catch (IOException e) {
            throw new RuntimeException("复制文件失败",e);
        }
        // FIXME 应该确认保存至数据库与复制文件，否则执行操作回滚
        //存储
        processModelTemplateRepository.save(processModelTemplate);
        return processModelTemplate.getId();
    }

    @Override
    public List<ProcessModelTemplate> getAll() {
        return (List<ProcessModelTemplate>) processModelTemplateRepository.findAll();
    }

    @Override
    public ProcessModelTemplate getById(Long id) {
        Optional<ProcessModelTemplate> result = processModelTemplateRepository.findById(id);
        return result.orElse(null);
    }
}
