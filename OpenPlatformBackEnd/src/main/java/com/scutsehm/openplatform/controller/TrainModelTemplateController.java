package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.POJO.VO.Result;
import com.scutsehm.openplatform.POJO.entity.TrainModelTemplate;
import com.scutsehm.openplatform.service.TrainModelTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * TrainModelTemplate的Controller
 * 管理训练模型模板信息的接口
 *
 * 基本该系统中controller不做常规参数校验外的任何事。
 * 仅仅作为http接入层。
 */

@RestController
@RequestMapping("/trainModelTemplate")
@Api(tags = "训练模板相关接口")
//TODO exception 是可以根据具体类型返回一些信息
public class TrainModelTemplateController {

    /**
     * 对应service
     */
    @Autowired
    private TrainModelTemplateService trainModelTemplateService;

    /**
     * 发布训练模型模板
     * @param trainModelTemplateInput
     * @return
     */
    @ApiOperation("添加训练模板")
//    @ApiImplicitParam(name = "TrainModelTemplate", value = "新增训练模板",paramType = "body", required = true, dataTypeClass = TrainModelTemplate.class)
    @PostMapping("/publish")
    public Result<Long> publishTrainModelTemplate(@RequestBody @Validated TrainModelTemplate trainModelTemplateInput){
        //TODO 没写VO导致jpa和mvc会共用valid，暂时对参数拦截能力较差，要么分组校验，要么写个VO
        Long templateId = trainModelTemplateService.publish(trainModelTemplateInput);
        return Result.<Long>OK().data(templateId).build();
    }

    /**
     * 返回所有结果
     * TODO 可以进行分页
     * @return
     */
    @ApiOperation("获取所有训练模板")
    @GetMapping("/getAll")
    public Result<List<TrainModelTemplate>> getAll(){
        return Result.<List<TrainModelTemplate>>OK()
                .data(trainModelTemplateService.getAll())
                .build();
    }

    /**
     * 根据ID返回相应模板，非restful风格接口，在url中以参数id形式携带
     * @param id 模板的id
     * @return
     */
    @ApiOperation("通过id查找训练模板")
    @GetMapping("/getById")
    public Result<TrainModelTemplate> getById(@RequestParam("id") Long id){
        TrainModelTemplate result = trainModelTemplateService.getById(id);
        if(result!=null){
            return Result.<TrainModelTemplate>OK().data(result).build();
        }else{
            return Result.<TrainModelTemplate>NOT_FOUND().build();
        }
    }
}
