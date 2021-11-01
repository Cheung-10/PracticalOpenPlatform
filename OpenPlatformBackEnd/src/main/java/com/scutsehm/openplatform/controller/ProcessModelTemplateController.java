package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.POJO.VO.Result;
import com.scutsehm.openplatform.POJO.entity.ProcessModelTemplate;
import com.scutsehm.openplatform.service.ProcessModelTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ProcessModelTemplate的Controller
 * 管理处理模型模板信息的接口
 *
 * 系统中controller基本不做常规参数校验外的任何事。
 * 仅仅作为http接入层。
 *
 * TrainModelTemplateController的复刻版，若有以后，把Train和Process合二为一吧（加个TaskType）
 */
@Api(tags = "处理模板相关接口")
@RestController
@RequestMapping("/processModelTemplate")
//TODO exception 是可以根据具体类型返回一些信息
public class ProcessModelTemplateController {

    /**
     * 对应service
     */
    @Autowired
    private ProcessModelTemplateService processModelTemplateService;

    /**
     * 发布处理模型模板
     * @param processModelTemplateInput
     * @return
     */
    @ApiOperation("添加处理模板")
    @PostMapping("/publish")
    public Result<Long> publishProcessModelTemplate(@RequestBody @Validated ProcessModelTemplate processModelTemplateInput){
        //TODO 没写VO导致jpa和mvc会共用valid，暂时对参数拦截能力较差，要么分组校验，要么写个VO
        Long templateId = processModelTemplateService.publish(processModelTemplateInput);
        return Result.<Long>OK().data(templateId).build();
    }

    /**
     * 返回所有结果
     * TODO 可以进行分页
     * @return
     */
    @ApiOperation("获取所有处理模板")
    @GetMapping("/getAll")
    public Result<List<ProcessModelTemplate>> getAll(){
        return Result.<List<ProcessModelTemplate>>OK()
                .data(processModelTemplateService.getAll())
                .build();
    }

    /**
     * 根据ID返回相应模板，非restful风格接口，在url中以参数id形式携带
     * @param id 模板的id
     * @return
     */
    @GetMapping("/getById")
    @ApiOperation("通过id查找处理模板")
    public Result<ProcessModelTemplate> getById(@RequestParam("id") Long id){
        ProcessModelTemplate result = processModelTemplateService.getById(id);
        if(result!=null){
            return Result.<ProcessModelTemplate>OK().data(result).build();
        }else{
            return Result.<ProcessModelTemplate>NOT_FOUND().build();
        }
    }
}
