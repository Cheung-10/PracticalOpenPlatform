package com.scutsehm.openplatform.controller;

import com.scutsehm.openplatform.POJO.VO.Result;
import com.scutsehm.openplatform.POJO.entity.RuntimeEnvironment;
import com.scutsehm.openplatform.POJO.entity.TrainModelTemplate;
import com.scutsehm.openplatform.service.RuntimeEnvironmentService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/environment")
@Api(tags = "运行环境相关接口")
public class RuntimeEnvironmentController {
    @Autowired
    private RuntimeEnvironmentService runtimeEnvironmentService;

    @PostMapping("/add")
    Result<Long> add(@Validated @RequestBody RuntimeEnvironment runtimeEnvironment) {
        Long id = runtimeEnvironmentService.add(runtimeEnvironment);
        return Result.<Long>OK().data(id).build();
    }

    @GetMapping("/getAll")
    Result<List<RuntimeEnvironment>> getAll() {
        List<RuntimeEnvironment> runtimeEnvironmentList = runtimeEnvironmentService.getAll();
        return Result.<List<RuntimeEnvironment>>OK().data(runtimeEnvironmentList).build();
    }

    @GetMapping("/getById")
    Result<RuntimeEnvironment> getById(@RequestParam(name = "id") Long id) {
        RuntimeEnvironment runtimeEnvironment = runtimeEnvironmentService.getById(id);
        if (runtimeEnvironment != null) {
            return Result.<RuntimeEnvironment>OK().data(runtimeEnvironment).build();
        } else {
            return Result.<RuntimeEnvironment>NOT_FOUND().build();
        }
    }
}
