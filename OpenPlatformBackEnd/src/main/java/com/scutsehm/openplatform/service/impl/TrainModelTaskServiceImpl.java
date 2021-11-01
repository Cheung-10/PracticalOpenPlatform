package com.scutsehm.openplatform.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.scutsehm.openplatform.POJO.entity.*;
import com.scutsehm.openplatform.POJO.enums.FileSpace;
import com.scutsehm.openplatform.POJO.enums.TaskStatus;
import com.scutsehm.openplatform.POJO.message.TaskResult;
import com.scutsehm.openplatform.dao.repository.*;
import com.scutsehm.openplatform.exception.BusinessException;
import com.scutsehm.openplatform.exception.exceptionEnum.CommonBusinessErrorCode;
import com.scutsehm.openplatform.exception.exceptionEnum.TrainModelTaskErrorCode;
import com.scutsehm.openplatform.exception.exceptionEnum.TrainModelTemplateErrorCode;
import com.scutsehm.openplatform.kubernetes.JobManager;
import com.scutsehm.openplatform.service.TrainModelTaskService;
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
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TrainModelTaskServiceImpl implements TrainModelTaskService {

    /**
     * TrainModelTemplate数据访问接口
     */
    @Autowired
    private TrainModelTemplateRepository trainModelTemplateRepository;

    /**
     * TrainModelTask数据访问接口
     */
    @Autowired
    private TrainModelTaskRepository trainModelTaskRepository;

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

    @Autowired
    private DeployTaskRepository deployTaskRepository;

    @Autowired
    private ProcessModelTemplateRepository processModelTemplateRepository;

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
    public String execute(TrainModelTask task) {
        //checkPath
        FileAndPathUtils.validate(task.getInputPathParameter().getValue());
        FileAndPathUtils.validate(task.getOutputPathParameter().getValue());
        //check id，deploy合法性
        //check id
        Long trainModelTemplateId = task.getTrainModelTemplateId();
        Optional<TrainModelTemplate> optional = trainModelTemplateRepository.findById(trainModelTemplateId);
        TrainModelTemplate template;
        if (!optional.isPresent()) {
            //模板不存在就抛异常
            throw new BusinessException(TrainModelTemplateErrorCode.NOT_FOUND);
        } else {
            template = optional.get();
        }
        //check deploy
        if (task.isDeploy()) {
            //对应模板不支持自动部署
            if (!template.isSupportDeploy()) {
                throw new BusinessException(TrainModelTaskErrorCode.NOT_SUPPORT_DEPLOY);
            }
        }
        //检查参数
        //TODO 这个工具还没做过单元测试
        if (!RuntimeParameterUtils.validate(template.getRuntimeParameterTemplateList(), task.getRuntimeParameterList())) {
            throw new BusinessException(TrainModelTaskErrorCode.RUNTIME_PARAMETER_ERROR);
        }

        //克隆备份路径
        PathParameter originInputParameter = ObjectUtil.cloneByStream(task.getInputPathParameter());
        PathParameter originOutputParameter = ObjectUtil.cloneByStream(task.getOutputPathParameter());

        //处理要进行自动部署,前置处理
        //old输出参数
        if (task.isDeploy()) {
            //生成处理模型路径
            String randomPath = "/" + FileAndPathUtils.getRandomName();
            //属性注入DeployTask
            DeployTask deployTask = task.getDeployTask();
            //写入处理模型输出信息
            deployTask.setProcessModelFolderPath(randomPath);
            deployTask.setTargetOutput(originOutputParameter.getValue());
            //更改task OutPut路径，取出template里面的outputTemplate的路径使用
            PathParameter newOutputParameter = new PathParameter();
            newOutputParameter.setName(originOutputParameter.getName());
            newOutputParameter.setValue(new SpacePath(FileSpace.PROCESSMODEL, randomPath));
            task.setOutputPathParameter(newOutputParameter);
        }

        //路径处理
        processPathParameter(task.getInputPathParameter());
        processPathParameter(task.getOutputPathParameter());

        //取出运行环境
        RuntimeEnvironment runtimeEnvironment;
        Optional<RuntimeEnvironment> runtimeEnvironmentOptional = runtimeEnvironmentRepository
                .findById(template.getRuntimeInformation().getRuntimeEnvironmentId());
        if (runtimeEnvironmentOptional.isPresent()) {
            runtimeEnvironment = runtimeEnvironmentOptional.get();
        } else {
            throw new RuntimeException("对应的RuntimeEnvironment id不存在，请检查数据库外键约束");
        }
        //分发
        TrainModelTask.DispatchMode dispatchMode = task.getDispatchMode();
        switch (dispatchMode) {
            case SHELL:
                executeTaskByShell(task, template);
                break;
            case KUBERNETES:
                executeTaskByKubernetes(task, template, runtimeEnvironment);
                break;
            default:
                throw new RuntimeException("请检查代码TrainModelTask.DispatchMode取值");
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
        trainModelTaskRepository.save(task);
        //返回taskId
        return task.getId();
    }

    /**
     * 主要涉及用户空间路径的改写
     *
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
    private void executeTaskByKubernetes(TrainModelTask task, TrainModelTemplate template, RuntimeEnvironment runtimeEnvironment) {
        //构造执行程序的空间路径信息
        SpacePath application = new SpacePath();
        application.setSpace(FileSpace.TRAINMODEL);
        application.setPath(template.getRuntimeInformation().getApplicationPath());

        //构造config
        String trainJobName = generateTrainJobName();
        JobManager.JobConfig config = JobManager.JobConfig.builder()
                .inputDataPath(task.getInputPathParameter())
                .outputDataPath(task.getOutputPathParameter())
                .applicationPath(application)
                .applicationEntry(template.getRuntimeInformation().getApplicationEntry())
                .jobName(trainJobName)
                .imageName(runtimeEnvironment.getName())
                .namespace("train")
                .parameterList(task.getRuntimeParameterList())
                .build();

        //执行
        Job job = jobManager.create(config);
        //检测状态
        jobManager.watchJobStatus(job);
        //task id设为与job name一致，靠job name判断对应train的task
        task.setId(trainJobName);
    }

    private void executeTaskByShell(TrainModelTask task, TrainModelTemplate template) {
        //TODO 记得在结束后发一条FAILED或SUCCEED的消息
        throw new RuntimeException("还不支持Shell调用");
    }

    /**
     * 产生随机训练job名字
     * TODO 必有并发性能问题
     *
     * @return 随机名
     */
    private String generateTrainJobName() {
        return "train-" + UUID.randomUUID();
    }

    @Override
    public void stop(String taskId) {
        saveLog(taskId);
        //分发
        TrainModelTask.DispatchMode dispatchMode = getDispatchMode(taskId);
        switch (dispatchMode) {
            case SHELL:
                stopByShell(taskId);
                break;
            case KUBERNETES:
                stopByKubernetes(taskId);
                break;
            default:
                throw new RuntimeException("请检查代码TrainModelTask.DispatchMode取值");
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
        jobManager.delete("train", taskId);
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

    /**
     * 获取日志
     *
     * @param taskId 操作的task的id
     * @return
     */
    @Override
    public String getLog(String taskId) {
        //先看看数据库里面有没有
        Optional<TaskLog> optional = taskLogRepository.findById(taskId);
        if (optional.isPresent()) {
            return optional.get().getContent();
        }
        //分发
        TrainModelTask.DispatchMode dispatchMode = getDispatchMode(taskId);
        switch (dispatchMode) {
            case SHELL:
                return getLogByShell(taskId);
            case KUBERNETES:
                return getLogByKubernetes(taskId);
            default:
                throw new RuntimeException("请检查代码TrainModelTask.DispatchMode取值");
        }
    }

    /**
     * kubernetes方式获取日志
     *
     * @param taskId 任务ID
     * @return 日志
     */
    private String getLogByKubernetes(String taskId) {
        return jobManager.getLog("train", taskId);
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
    private TrainModelTask.DispatchMode getDispatchMode(String taskId) {
        Optional<TrainModelTask> optional = trainModelTaskRepository.findById(taskId);
        if (optional.isPresent()) {
            return optional.get().getDispatchMode();
        } else {
            throw new BusinessException(CommonBusinessErrorCode.NOT_FOUND);
        }
    }

    @Override
    public TrainModelTask getById(String taskId) {
        Optional<TrainModelTask> optional = trainModelTaskRepository.findById(taskId);
        return optional.orElse(null);
    }

    @Override
    public List<TrainModelTask> getAllByCurrentUser() {
        String currentUser = UserUtil.getUsername();
        return trainModelTaskRepository.findAllByPublisher(currentUser);
    }

    /**
     * trainTask 完成之后异步调用
     *
     * @param taskResult
     */
    @RabbitListener(queues = "TrainModelTaskQueue")
    @Transactional
    public void onTrainTaskFinish(TaskResult taskResult) {
        //推送消息
        String userId = trainModelTaskRepository.findPublisherById(taskResult.getTaskName());
        messageManager.sendNormalMessage("训练任务\"" + taskResult.getTaskName() + "\" 结果: " + taskResult.getStatus(), userId);
        //更改状态
        trainModelTaskRepository.updateTaskStatusById(taskResult.getStatus(), taskResult.getTaskName());
        if (taskResult.getStatus() == TaskStatus.SUCCEED) {
            Long deployTaskId = trainModelTaskRepository.findDeployTaskIdById(taskResult.getTaskName());
            //判断是否需要部署
            if (deployTaskId == null) {
                return;
            }
            Optional<DeployTask> deployTaskOptional = deployTaskRepository.findById(deployTaskId);
            DeployTask deployTask = deployTaskOptional.get();
            SpacePath processModelSpacePath = new SpacePath(FileSpace.PROCESSMODEL, deployTask.getProcessModelFolderPath());
            //先复制结果给用户
            try {
                FileAndPathUtils.copy(processModelSpacePath, deployTask.getTargetOutput());
            } catch (IOException e) {
                //TODO 复制结果失败
                e.printStackTrace();
            }

            //获得template信息
            TrainModelTask trainModelTask = trainModelTaskRepository.findTrainModelTaskByDeployTaskId(deployTaskId);
            Long trainModelTemplateId = trainModelTask.getTrainModelTemplateId();
            Optional<TrainModelTemplate> option = trainModelTemplateRepository.findById(trainModelTemplateId);
            TrainModelTemplate trainModelTemplate = option.get();
            DeployTemplate deployTemplate = trainModelTemplate.getDeployTemplate();

            //复制程序
            String applicationPath = trainModelTemplate.getRuntimeInformation().getApplicationPath();
            try {
                FileAndPathUtils.copy(new SpacePath(FileSpace.TRAINMODEL, applicationPath), processModelSpacePath);
            } catch (IOException e) {
                throw new RuntimeException("复制文件失败，自动部署失败");
                //TODO 发送自动部署失败消息
            }

            //insert ProcessModelTemplate
            //SpringDataJPA查出来的对象如果被更改了是会自动保存的。这一点挺鸡肋的
            //TODO 可以考虑用ObjectUtil.cloneByStream来做个深度拷贝，更正确的做法是把DO，DTO分开
            ProcessModelTemplate processModelTemplate = new ProcessModelTemplate();
            processModelTemplate.setName(deployTask.getName());
            processModelTemplate.setDescription(deployTask.getDescription());
            processModelTemplate.setInputPathParameterTemplate(deployTemplate.getInputPathParameterTemplate());
            processModelTemplate.setOutputPathParameterTemplate(deployTemplate.getOutputPathParameterTemplate());
            RuntimeInformation runtimeInformation = new RuntimeInformation();
            runtimeInformation.setRuntimeEnvironmentId(trainModelTemplate.getRuntimeInformation().getRuntimeEnvironmentId());
            runtimeInformation.setApplicationPath(deployTask.getProcessModelFolderPath());
            runtimeInformation.setApplicationEntry(deployTemplate.getApplicationEntry());
            processModelTemplate.setRuntimeInformation(runtimeInformation);
            List<RuntimeParameterTemplate> runtimeParameterTemplateListFrom = deployTemplate.getRuntimeParameterTemplateList();
            ArrayList<RuntimeParameterTemplate> runtimeParameterTemplateListTo = new ArrayList<>(runtimeParameterTemplateListFrom.size());
            for (RuntimeParameterTemplate from : runtimeParameterTemplateListFrom) {
                RuntimeParameterTemplate to = new RuntimeParameterTemplate();
                BeanUtils.copyProperties(from, to);
                to.setId(null);
                runtimeParameterTemplateListTo.add(to);
            }
            processModelTemplate.setRuntimeParameterTemplateList(runtimeParameterTemplateListTo);
            processModelTemplateRepository.save(processModelTemplate);

            messageManager.sendNormalMessage("自动部署完成，处理模型id:\"" + processModelTemplate.getId() + "\"", userId);
        }

    }
}
