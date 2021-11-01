package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.POJO.VO.Result;
import com.scutsehm.openplatform.POJO.entity.TrainModelTask;
import com.scutsehm.openplatform.service.TrainModelTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理训练模型任务的接口
 */
@Api(tags = "训练任务相关接口")
@RestController
@RequestMapping("/trainModelTask")
public class TrainModelTaskController {

    @Autowired
    private TrainModelTaskService trainModelTaskService;

    @ApiOperation("新建训练任务并执行")
    @PostMapping("/execute")
    public Result<String> execute(@RequestBody @Validated TrainModelTask task){
        String taskId = trainModelTaskService.execute(task);
        return Result.<String>OK().data(taskId).build();
    }

    @ApiOperation("停止一个训练任务")
    @PostMapping("/stop")
    public Result<String> stop(@RequestParam(name = "taskId") String taskId){
        trainModelTaskService.stop(taskId);
        return Result.<String>OK().data("成功停止").build();
    }

    @ApiOperation("获取训练任务的日志")
    @GetMapping("/getLog")
    public Result<String> getLog(@RequestParam(name = "taskId") String taskId){
        String log = trainModelTaskService.getLog(taskId);
        return Result.<String>OK().data(log).build();
    }

    @ApiOperation("通过id查找训练任务")
    @GetMapping("/getById")
    public Result<TrainModelTask> getById(@RequestParam(name = "taskId") String taskId){
        TrainModelTask task = trainModelTaskService.getById(taskId);
        return Result.<TrainModelTask>OK().data(task).build();

    }

    @ApiOperation("获得当前用户登录的所有训练任务")
    @GetMapping("/getAllByCurrentUser")
    public Result<List<TrainModelTask>> getAllByCurrentUser(){
        List<TrainModelTask> trainModelTaskList = trainModelTaskService.getAllByCurrentUser();
        return Result.<List<TrainModelTask>>OK().data(trainModelTaskList).build();
    }
}
