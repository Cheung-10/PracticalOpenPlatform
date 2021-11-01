package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.POJO.VO.Result;
import com.scutsehm.openplatform.POJO.entity.ProcessModelTask;
import com.scutsehm.openplatform.service.ProcessModelTaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理处理模型任务的接口
 */
@Api(tags = "处理任务相关接口")
@RestController
@RequestMapping("/processModelTask")
public class ProcessModelTaskController {

    @Autowired
    private ProcessModelTaskService processModelTaskService;

    @ApiOperation("新建处理任务并执行")
    @PostMapping("/execute")
    public Result<String> publish(@RequestBody @Validated ProcessModelTask task){
        String taskId = processModelTaskService.execute(task);
        return Result.<String>OK().data(taskId).build();
    }

    @ApiOperation("停止一个处理任务")
    @PostMapping("/stop")
    public Result<String> stop(@RequestParam(name = "taskId") String taskId){
        processModelTaskService.stop(taskId);
        return Result.<String>OK().data("成功停止").build();
    }

    @ApiOperation("获取处理任务的日志")
    @GetMapping("/getLog")
    public Result<String> getLog(@RequestParam(name = "taskId") String taskId){
        String log = processModelTaskService.getLog(taskId);
        return Result.<String>OK().data(log).build();
    }

    @ApiOperation("通过id查找处理任务")
    @GetMapping("/getById")
    public Result<ProcessModelTask> getById(@RequestParam(name = "taskId") String taskId){
        ProcessModelTask task = processModelTaskService.getById(taskId);
        return Result.<ProcessModelTask>OK().data(task).build();

    }

    @ApiOperation("获得当前用户登录的所有处理任务")
    @GetMapping("/getAllByCurrentUser")
    public Result<List<ProcessModelTask>> getAllByCurrentUser(){
        List<ProcessModelTask> processModelTaskList = processModelTaskService.getAllByCurrentUser();
        return Result.<List<ProcessModelTask>>OK().data(processModelTaskList).build();
    }
}
