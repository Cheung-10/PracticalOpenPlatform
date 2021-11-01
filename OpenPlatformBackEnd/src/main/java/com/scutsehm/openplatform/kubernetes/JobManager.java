package com.scutsehm.openplatform.kubernetes;

import com.scutsehm.openplatform.POJO.entity.*;
import com.scutsehm.openplatform.POJO.enums.FileSpace;
import com.scutsehm.openplatform.POJO.enums.TaskStatus;
import com.scutsehm.openplatform.POJO.message.TaskResult;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.JobStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.fabric8.kubernetes.client.dsl.ScalableResource;
import io.fabric8.kubernetes.client.dsl.internal.AbstractWatchManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class JobManager {
    /**
     * k8s核心
     */
    @Autowired
    private KubernetesManager kubernetesManager;

    /**
     * nfs服务器地址
     */
    @Value("${open-platform.nfs.server}")
    private String nfsServer;

    /**
     * 各空间在server的mount节点
     */
    @Value("${open-platform.nfs.mount-point.private}")
    private String privateMount;
    @Value("${open-platform.nfs.mount-point.data}")
    private String dataMount;
    @Value("${open-platform.nfs.mount-point.share}")
    private String shareMount;
    @Value("${open-platform.nfs.mount-point.process-model}")
    private String processModelMount;
    @Value("${open-platform.nfs.mount-point.train-model}")
    private String trainModelMount;

    /**
     * client的数据的mount点
     */
    private final String inputDataMount = "/input-data";
    private final String outputDataMount = "/output-data";
    /**
     * client的程序的mount点
     */
    private final String processMount = "/process";

    /**
     * 管理活跃的Watchers
     */
    private final ConcurrentHashMap<String, Watcher<Job>> activeWatchers = new ConcurrentHashMap<>();

    /**
     * 用于发送消息
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 返回不同空间的根mount节点
     *
     * @param space 空间
     * @return mount路径
     */
    private String getMountRootByFileSpace(FileSpace space) {
        switch (space) {
            case PRIVATE:
                return privateMount;
            case DATA:
                return dataMount;
            case SHARE:
                return shareMount;
            case TRAINMODEL:
                return trainModelMount;
            case PROCESSMODEL:
                return processModelMount;
            default:
                throw new RuntimeException("请检查FileSpace取值");
        }
    }

    /**
     * 和FileAndPath的路径有何不同？ FileAndPath获取的是绝对路径，本地中使用。
     * 该函数返回的是nfs server中要mount的最终路径
     *
     * @param spacePath
     * @return
     */
    private String getMountPath(SpacePath spacePath) {
        return getMountRootByFileSpace(spacePath.getSpace()) + spacePath.getPath();
    }

    /**
     * 获取容器中的程序入口
     *
     * @param applicationEntry 程序相对入口
     * @return 入口
     */
    private String getApplicationEntry( String applicationEntry) {
        return processMount + "/" + applicationEntry;
    }

    /**
     * 获取输入路径参数
     *
     * @param input 某空间下的相对输入路径
     * @return 容器中的输入路径
     */
    private String getInputDataPath(String input) {
        return inputDataMount + input;
    }

    /**
     * 获取输出路径参数
     *
     * @param output 某空间下的相对输出路径
     * @return 容器中的输出路径
     */
    private String getOutputDataPath(String output) {
        return outputDataMount + output;
    }

    /**
     * 获取执行容器启动执行命令
     *
     * @param jobConfig
     * @return
     */
    private List<String> getCommand(JobConfig jobConfig) {
        //定义(最大)初始容量避免扩容
        int parameterSize = jobConfig.getParameterList().size();
        ArrayList<String> command = new ArrayList<>(parameterSize + 4);
        //启动命令
        command.add("python");
        //程序入口
        command.add(getApplicationEntry( jobConfig.getApplicationEntry()));
        //处理路径参数
        command.add(getParameter(jobConfig.getInputDataPath().getName(),
                getInputDataPath(jobConfig.getInputDataPath().getValue().getPath())));
        command.add(getParameter(jobConfig.getOutputDataPath().getName(),
                getOutputDataPath(jobConfig.getOutputDataPath().getValue().getPath())));
        //处理普通参数
        jobConfig.getParameterList().forEach(runtimeParameter -> {
            //若实际传参为空，不构造调用参数
            if (runtimeParameter.getValue() != null && !runtimeParameter.getValue().isEmpty()) {
                command.add(getParameter(runtimeParameter.getName(), runtimeParameter.getValue()));
            }
        });
        return command;
    }

    /**
     * 返回KV形式参数
     */
    private String getParameter(String key, String value) {
        return "--" + key + "=" + value;
    }

    /**
     * 创建并运行job
     *
     * @param jobConfig
     * @return
     */
    public Job create(JobConfig jobConfig) {
        //storage名字，在配置中起作用，只要在VolumeMount和Volume中一样即可
        String inputDataStorage = "input-data-storage";
        String outputDataStorage = "output-data-storage";
        String processStorage = "process-storage";
        //k8s客户端
        KubernetesClient client = kubernetesManager.getClient();
        //3mount
        VolumeMount inputDataVolumeMount = new VolumeMountBuilder().withName(inputDataStorage)
                .withMountPath(inputDataMount)
                .build();
        VolumeMount outputDataVolumeMount = new VolumeMountBuilder().withName(outputDataStorage)
                .withMountPath(outputDataMount)
                .build();
        VolumeMount processVolumeMount = new VolumeMountBuilder().withName(processStorage)
                .withMountPath(processMount)
                .build();
        //3volume
        Volume inputDataVolume = new VolumeBuilder().withName(inputDataStorage)
                .withNewNfs()
                .withPath(getMountRootByFileSpace(jobConfig.getInputDataPath().getValue().getSpace()))
                .withServer(nfsServer)
                .endNfs()
                .build();
        Volume outputDataVolume = new VolumeBuilder().withName(outputDataStorage)
                .withNewNfs()
                .withPath(getMountRootByFileSpace(jobConfig.getOutputDataPath().getValue().getSpace()))
                .withServer(nfsServer)
                .endNfs()
                .build();
        Volume processVolume = new VolumeBuilder().withName(processStorage)
                .withNewNfs()
                .withPath(getMountRootByFileSpace(jobConfig.getApplicationPath().getSpace())+jobConfig.getApplicationPath().getPath())
                .withServer(nfsServer)
                .endNfs()
                .build();
        Container container = new ContainerBuilder().withName(jobConfig.getJobName())
                .withImage(jobConfig.getImageName())
                .withImagePullPolicy("IfNotPresent")
                .withCommand(getCommand(jobConfig))
                .withVolumeMounts(inputDataVolumeMount, outputDataVolumeMount, processVolumeMount)
                .build();
        Job job = new JobBuilder().withApiVersion("batch/v1")
                .withKind("Job")
                .withNewMetadata()
                .withName(jobConfig.getJobName())
                .withNamespace(jobConfig.getNamespace())
                .endMetadata()
                .withNewSpec()
                .withNewTemplate()
                .withNewMetadata()
                .withName(jobConfig.getJobName())
                .endMetadata()
                .withNewSpec()
//                .withNewRestartPolicy("OnFailure")
                .withNewRestartPolicy("Never")
                .withContainers(container)
                .withVolumes(inputDataVolume, outputDataVolume, processVolume)
                .endSpec()
                .endTemplate()
                .endSpec()
                .build();

        return client.batch().jobs().create(job);
    }

    /**
     * 获取所有日志
     * @param namespace
     * @param taskId
     * @return
     */
    public String getLog(String namespace, String taskId) {
        KubernetesClient client = kubernetesManager.getClient();
        return client.batch().jobs().inNamespace(namespace).withName(taskId).getLog();
    }

    /**
     * job删除
     * @param namespace
     * @param taskId
     */
    public void delete(String namespace, String taskId) {
        KubernetesClient client = kubernetesManager.getClient();
        ScalableResource<Job> job = client.batch().jobs().inNamespace(namespace).withName(taskId);
        //若查不到job，跳过就行
        if(job.get()!=null){
            job.delete();
        }
    }


    /**
     * JobConfig内部类
     */
    @Data
    @Builder
    @AllArgsConstructor
    public static class JobConfig {
        /**
         * job名字，不可重复的
         */
        private String jobName;

        /**
         * namespace ,取值一般是train或者process
         */
        private String namespace;

        /**
         * 运行镜像名字
         */
        private String imageName;

        /**
         * 程序路径
         */
        private SpacePath applicationPath;

        /**
         * 程序入口
         */
        private String applicationEntry;

        /**
         * 输入数据路径
         */
        private PathParameter inputDataPath;

        /**
         * 输出数据路径
         */
        private PathParameter outputDataPath;

        /**
         * 运行参数
         */
        List<RuntimeParameter> parameterList;
    }

    /**
     * 监控job的状态
     *
     * @param job 被监控的job
     */
    public void watchJobStatus(Job job) {
        //k8s客户端
        KubernetesClient client = kubernetesManager.getClient();
        ScalableResource<Job> watchJob = client
                .batch()
                .jobs()
                .inNamespace(job.getMetadata().getNamespace())
                .withName(job.getMetadata().getName());
        watchJob.watch(new Watcher<Job>() {
            @Override
            public void eventReceived(Action action, Job job) {
                String name = job.getMetadata().getName();
                switch (action) {
                    case MODIFIED:
                        JobStatus status = job.getStatus();
                        if (!isActive(status)) {
                            if (isSucceeded(status)) {
                                sendToMQ(TaskStatus.SUCCEED, name);
                            } else if (isFailed(status)) {
                                sendToMQ(TaskStatus.FAILED, name);
                            } else {
                                throw new RuntimeException("未知状态");
                            }
                            //终止Watcher
                            //TODO 还不清楚Watcher什么时候会关闭，手动关闭
                            deleteWatch(name);
                        }
                        break;
                    case DELETED:
                        sendToMQ(TaskStatus.STOPPED, name);
                        deleteWatch(name);
                        break;
                }
            }

            @Override
            public void onClose(WatcherException e) {

            }
        });
    }

    private void deleteWatch(String jobName) {
        Watcher<Job> watcher = activeWatchers.get(jobName);
        if (watcher != null) {
            if (watcher instanceof AbstractWatchManager) {
                ((AbstractWatchManager) watcher).close();
                activeWatchers.remove(jobName);
            } else {
                throw new RuntimeException("程序设计Error，查看watcher类型");
            }
        }
    }

    private void sendToMQ(TaskStatus status, String jobName) {
        String exchangeName;
        if (jobName.startsWith("train")) {
            exchangeName = "TrainModelTaskExchange";
        } else if (jobName.startsWith("process")) {
            exchangeName = "ProcessModelTaskExchange";
        } else {
            throw new RuntimeException("检查jobName合法性");
        }
        rabbitTemplate.convertAndSend(exchangeName, "", new TaskResult(jobName, status));
    }


    /**
     * 判断是否还在执行
     *
     * @param status Job状态
     * @return 是否还在执行
     */
    private boolean isActive(JobStatus status) {
        Integer active = status.getActive();
        return active != null && active >= 1;
    }

    /**
     * 判断job是否成功执行
     *
     * @param status Job状态
     * @return 是否成功执行
     */
    private boolean isSucceeded(JobStatus status) {
        Integer succeeded = status.getSucceeded();
        return succeeded != null && succeeded >= 1;
    }

    /**
     * 判断Job是否有过失败状态，注意可能先是失败重试后成功。
     *
     * @param status Job状态
     * @return 是否曾经失败
     */
    private boolean isFailed(JobStatus status) {
        Integer failed = status.getFailed();
        return failed != null && failed >= 1;
    }
}
