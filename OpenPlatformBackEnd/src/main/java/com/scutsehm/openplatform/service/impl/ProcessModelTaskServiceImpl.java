package com.scutsehm.openplatform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.scutsehm.openplatform.POJO.entity.*;
import com.scutsehm.openplatform.POJO.enums.FileSpace;
import com.scutsehm.openplatform.POJO.enums.TaskStatus;
import com.scutsehm.openplatform.POJO.message.TaskResult;
import com.scutsehm.openplatform.dao.repository.ProcessModelTaskRepository;
import com.scutsehm.openplatform.dao.repository.ProcessModelTemplateRepository;
import com.scutsehm.openplatform.dao.repository.RuntimeEnvironmentRepository;
import com.scutsehm.openplatform.dao.repository.TaskLogRepository;
import com.scutsehm.openplatform.exception.BusinessException;
import com.scutsehm.openplatform.exception.exceptionEnum.CommonBusinessErrorCode;
import com.scutsehm.openplatform.kubernetes.JobManager;
import com.scutsehm.openplatform.service.ProcessModelTaskService;
import com.scutsehm.openplatform.util.FileAndPathUtils;
import com.scutsehm.openplatform.util.RuntimeParameterUtils;
import com.scutsehm.openplatform.util.UserUtil;
import com.scutsehm.openplatform.websocket.MessageManager;
import io.fabric8.kubernetes.api.model.batch.Job;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProcessModelTaskServiceImpl implements ProcessModelTaskService {

    /**
     * ProcessModelTemplate数据访问接口
     */
    @Autowired
    private ProcessModelTemplateRepository processModelTemplateRepository;
    /**
     * ProcessModelTask数据访问接口
     */
    @Autowired
    private ProcessModelTaskRepository processModelTaskRepository;
    /**
     * 主要用于查镜像
     */
    @Autowired
    private RuntimeEnvironmentRepository runtimeEnvironmentRepository;
    /**
     * 日志DAO
     */
    @Autowired
    private TaskLogRepository taskLogRepository;
    /**
     * k8s job 核心
     */
    @Autowired
    private JobManager jobManager;

    /**
     * 消息管理器
     */
    @Autowired
    private MessageManager messageManager;

    /**
     * 执行任务
     * 遇到报错回滚数据库
     *
     * @param task 要执行的task
     * @return
     */
    @Override
    @Transactional
    public String execute(ProcessModelTask task) {
        //checkPath
        FileAndPathUtils.validate(task.getInputPathParameter().getValue());
        FileAndPathUtils.validate(task.getOutputPathParameter().getValue());
        //check id，deploy合法性
        //check id
        Long processModelTemplateId = task.getProcessModelTemplateId();
        Optional<ProcessModelTemplate> optional = processModelTemplateRepository.findById(processModelTemplateId);
        ProcessModelTemplate template = null;
        if (!optional.isPresent()) {
            //模板不存在就抛异常
//            throw new BusinessException(ProcessModelTemplateErrorCode.NOT_FOUND);
        } else {
            template = optional.get();
        }
        //检查参数
        if (!RuntimeParameterUtils.validate(template.getRuntimeParameterTemplateList(), task.getRuntimeParameterList())) {
//            throw new BusinessException(ProcessModelTaskErrorCode.RUNTIME_PARAMETER_ERROR);
        }

        //克隆备份路径
        PathParameter originInputParameter = ObjectUtil.cloneByStream(task.getInputPathParameter());
        PathParameter originOutputParameter = ObjectUtil.cloneByStream(task.getOutputPathParameter());

        //路径处理
        processPathParameter(task.getInputPathParameter());
        processPathParameter(task.getOutputPathParameter());

        //取出运行环境
        RuntimeEnvironment runtimeEnvironment = null;
        Optional<RuntimeEnvironment> runtimeEnvironmentOptional = runtimeEnvironmentRepository
                .findById(template.getRuntimeInformation().getRuntimeEnvironmentId());
        if (runtimeEnvironmentOptional.isPresent()) {
            runtimeEnvironment = runtimeEnvironmentOptional.get();
        } else {
            throw new RuntimeException("对应的RuntimeEnvironment id不存在，请检查数据库外键约束");
        }
        //分发
        ProcessModelTask.DispatchMode dispatchMode = task.getDispatchMode();
        switch (dispatchMode) {
            case SHELL:
                executeTaskByShell(task, template);
                break;
            case KUBERNETES:
                executeTaskByKubernetes(task, template, runtimeEnvironment);
                break;
            default:
                throw new RuntimeException("请检查代码ProcessModelTask.DispatchMode取值");
        }

        //执行成功，存库
        //初始化状态
        task.setTaskStatus(TaskStatus.RUNNING);
        //设置任务提交者
        task.setPublisher(UserUtil.getUsername());
        //恢复路径
        task.setInputPathParameter(originInputParameter);
        task.setOutputPathParameter(originOutputParameter);
        //存储任务
        processModelTaskRepository.save(task);
        return task.getId();
    }

    /**
     * 将PathParameter 中的SpacePath.path改为绝对路径，以便接下来使用
     * @param path 真实路径
     */
    private void processPathParameter(PathParameter path) {
        FileSpace space = path.getValue().getSpace();
        if (space == FileSpace.PRIVATE) {
            String originPath = path.getValue().getPath();
            path.getValue().setPath(UserUtil.getUserPath() + originPath);
        }
    }

    /**
     * 以k8s调度的方式进行调度
     * TODO 如果是设计模式来看的话，可以采用策略模式+模板模式进行重构
     *
     * @param task               要执行的任务
     * @param template           对应的模板
     * @param runtimeEnvironment 对应的运行环境
     */
    private void executeTaskByKubernetes(ProcessModelTask task, ProcessModelTemplate template, RuntimeEnvironment runtimeEnvironment) {
        //构造执行程序的空间路径信息
        SpacePath application = new SpacePath();
        application.setSpace(FileSpace.PROCESSMODEL);
        application.setPath(template.getRuntimeInformation().getApplicationPath());

        //构造config
        String processJobName = generateProcessJobName();
        JobManager.JobConfig config = JobManager.JobConfig.builder()
                .inputDataPath(task.getInputPathParameter())
                .outputDataPath(task.getOutputPathParameter())
                .applicationPath(application)
                .applicationEntry(template.getRuntimeInformation().getApplicationEntry())
                .jobName(processJobName)
                .imageName(runtimeEnvironment.getName())
                .namespace("process")
                .parameterList(task.getRuntimeParameterList())
                .build();

        //执行
        Job job = jobManager.create(config);
        //检测状态
        jobManager.watchJobStatus(job);
        //task id设为与job name一致，靠job name判断对应process的task
        task.setId(processJobName);
    }

    private void executeTaskByShell(ProcessModelTask task, ProcessModelTemplate template) {
        throw new RuntimeException("还不支持Shell调用");
    }

    /**
     * 产生随机处理job名字
     * TODO 必有并发性能问题
     *
     * @return 随机名
     */
    private String generateProcessJobName() {
        return "process-" + UUID.randomUUID();
    }

    @Override
    public void stop(String taskId) {
        saveLog(taskId);
        //分发
        ProcessModelTask.DispatchMode dispatchMode = getDispatchMode(taskId);
        switch (dispatchMode) {
            case SHELL:
                stopByShell(taskId);
                break;
            case KUBERNETES:
                stopByKubernetes(taskId);
                break;
            default:
                throw new RuntimeException("请检查代码ProcessModelTask.DispatchMode取值");
        }
    }

    private void stopByShell(String taskId) {
        //TODO 记得发一条异步消息STOPPED
    }

    /**
     * 停止任务
     *
     * @param taskId
     */
    private void stopByKubernetes(String taskId) {
        jobManager.delete("process", taskId);
    }

    /**
     * 保存日志
     *
     * @param taskId
     */
    private void saveLog(String taskId) {
        String log = getLog(taskId);
        //要判断日志不能为空，以免空日志覆盖了有效日志
        if (!StringUtils.isEmpty(taskId)) {
            taskLogRepository.save(new TaskLog(taskId, log));
        }
    }

    @Override
    public String getLog(String taskId) {
        //先看看数据库里面有没有
        Optional<TaskLog> optional = taskLogRepository.findById(taskId);
        if (optional.isPresent()) {
            return optional.get().getContent();
        }
        //分发
        ProcessModelTask.DispatchMode dispatchMode = getDispatchMode(taskId);
        switch (dispatchMode) {
            case SHELL:
                return getLogByShell(taskId);
            case KUBERNETES:
                return getLogByKubernetes(taskId);
            default:
                throw new RuntimeException("请检查代码ProcessModelTask.DispatchMode取值");
        }
    }

    /**
     * kubernetes方式获取日志
     *
     * @param taskId 任务ID
     * @return 日志
     */
    private String getLogByKubernetes(String taskId) {
        return jobManager.getLog("process", taskId);
    }

    private String getLogByShell(String taskId) {
        return null;
    }

    /**
     * 获得调度方式
     *
     * @param taskId
     * @return
     */
    private ProcessModelTask.DispatchMode getDispatchMode(String taskId) {
        Optional<ProcessModelTask> optional = processModelTaskRepository.findById(taskId);
        if (optional.isPresent()) {
            return optional.get().getDispatchMode();
        } else {
            throw new BusinessException(CommonBusinessErrorCode.NOT_FOUND);
        }
    }

    @Override
    public ProcessModelTask getById(String taskId) {
        Optional<ProcessModelTask> optional = processModelTaskRepository.findById(taskId);
        return optional.orElse(null);
    }

    @Override
    public List<ProcessModelTask> getAllByCurrentUser() {
        String currentUser = UserUtil.getUsername();
        return processModelTaskRepository.findAllByPublisher(currentUser);
    }

    /**
     * processTask 完成之后异步调用
     *
     * @param taskResult
     */
    @RabbitListener(queues = "ProcessModelTaskQueue")
    @Transactional
    public void onProcessTaskFinish(TaskResult taskResult) {
        //推送消息
        String userId = processModelTaskRepository.findPublisherById(taskResult.getTaskName());
        messageManager.sendNormalMessage("处理任务\"" + taskResult.getTaskName() + "\" 结果: " + taskResult.getStatus(), userId);
        //更新数据库状态
        processModelTaskRepository.updateTaskStatusById(taskResult.getStatus(), taskResult.getTaskName());
    }
}
