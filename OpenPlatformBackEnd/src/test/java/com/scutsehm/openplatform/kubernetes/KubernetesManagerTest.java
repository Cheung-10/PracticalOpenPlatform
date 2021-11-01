package com.scutsehm.openplatform.kubernetes;

import com.scutsehm.openplatform.POJO.entity.PathParameter;
import com.scutsehm.openplatform.POJO.entity.RuntimeParameter;
import com.scutsehm.openplatform.POJO.entity.SpacePath;
import com.scutsehm.openplatform.POJO.enums.FileSpace;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.batch.Job;
import io.fabric8.kubernetes.api.model.batch.JobBuilder;
import io.fabric8.kubernetes.api.model.batch.JobStatus;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.Watch;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.WatcherException;
import io.fabric8.kubernetes.client.dsl.DiscoveryAPIGroupDSL;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.ScalableResource;
import io.fabric8.kubernetes.client.dsl.internal.AbstractWatchManager;
import io.fabric8.kubernetes.client.dsl.internal.WatchConnectionManager;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.io.Reader;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

//@SpringBootTest
public class KubernetesManagerTest {
    @Autowired
    private KubernetesManager kubernetesManager;
    @Autowired
    private KubernetesConfig config;
    @Autowired
    private JobManager jobManager;

//    @Test
    public void create(){
        KubernetesClient client = kubernetesManager.getClient();
        VolumeMount privateMount = new VolumeMountBuilder().withName("private-storage")
                .withMountPath("/private")
                .build();
        VolumeMount publicMount = new VolumeMountBuilder().withName("process-storage")
                .withMountPath("/process")
                .build();
        Volume privateVolume = new VolumeBuilder().withName("private-storage")
                .withNewNfs()
                .withPath("/home/nfsboot/private/user1")
                .withServer("192.168.2.184")
                .endNfs()
                .build();
        Volume publicVolume = new VolumeBuilder().withName("process-storage")
                .withNewNfs()
                .withPath("/home/nfsboot/public/process/test1")
                .withServer("192.168.2.184")
                .endNfs()
                .build();
        Container container = new ContainerBuilder().withName("job1")
                .withImage("python")
                .withImagePullPolicy("IfNotPresent")
                .withCommand(Lists.list("python", "/process/test1.py", "25"))
                .withVolumeMounts(privateMount, publicMount)
                .build();
        Job job = new JobBuilder().withApiVersion("batch/v1")
                .withKind("Job")
                .withNewMetadata()
                    .withName("test-job")
                    .withNamespace("train")
                .endMetadata()
                .withNewSpec()
                    .withNewTemplate()
                        .withNewMetadata()
                            .withName("test-job123")
                        .endMetadata()
                        .withNewSpec()
                            .withRestartPolicy("Never")
                            .withContainers(container)
                            .withVolumes(privateVolume,publicVolume)
                        .endSpec()
                    .endTemplate()
                .endSpec()
                .build();

        Job job1 = client.batch().jobs().create(job);
        job1.getStatus();
        String jobName = "test-job";

        Job test = client.batch().jobs().inNamespace("train").withName(jobName).get();
        JobStatus status = test.getStatus();
        System.out.println(status);

    }

//    @Test
    public void create2(){
        PathParameter input = new PathParameter();
        SpacePath inputSpacePath = new SpacePath();
        inputSpacePath.setPath("/user1/data");
        inputSpacePath.setSpace(FileSpace.PRIVATE);
        input.setName("testInput");
        input.setValue(inputSpacePath);

        PathParameter output = new PathParameter();
        SpacePath outputSpacePath = new SpacePath();
        outputSpacePath.setPath("/user1/output");
        outputSpacePath.setSpace(FileSpace.PRIVATE);
        output.setName("testOutput");
        output.setValue(outputSpacePath);

        SpacePath application = new SpacePath();
        application.setSpace(FileSpace.TRAINMODEL);
        application.setPath("/test1");

        RuntimeParameter times = new RuntimeParameter();
        times.setName("times");
        times.setValue("50");

        JobManager.JobConfig config = JobManager.JobConfig.builder()
                .inputDataPath(input)
                .outputDataPath(output)
                .applicationPath(application)
                .applicationEntry("test1.py")
                .jobName("test-job")
                .imageName("python")
                .namespace("train")
                .parameterList(Collections.singletonList(times)).build();

        Job job = jobManager.create(config);
        System.out.println(job);
    }

//    @Test
    public void testWatch() throws InterruptedException {
        final CountDownLatch close = new CountDownLatch(1);
        String jobName = "test-job";
        KubernetesClient client = kubernetesManager.getClient();
        Job test = client.batch().jobs().inNamespace("train").withName(jobName).get();
        JobStatus status = test.getStatus();
        System.out.println(status);
        ScalableResource<Job> train = client.batch().jobs().inNamespace("train").withName(jobName);

        AbstractWatchManager watch = (AbstractWatchManager) train.watch(new Watcher<Job>() {
            @Override
            public void eventReceived(Action action, Job job) {
                System.out.println(action);
                System.out.println(job.getStatus());
            }

            @Override
            public void onClose(WatcherException e) {
                System.out.println("END");
                close.countDown();
            }
        });

        Thread.sleep(3000);
        System.out.println("BEGIN CLOSE");
        watch.close();
        System.out.println("WAITING");
        close.await();
        System.out.println("END");

//
//        List<Pod> pods = client.pods().withLabel("job-name", jobName).list().getItems();
//        for(Pod pod: pods){
//            System.out.println(pod.getMetadata().getName());
//            System.out.println(pod);
//            PodResource<Pod> podPodResource = client.pods().inNamespace("train").withName(pod.getMetadata().getName());
//            Watch watch = podPodResource.watch(new Watcher<Pod>() {
//                @Override
//                public void eventReceived(Action action, Pod pod) {
//
//                }
//
//                @Override
//                public void onClose(WatcherException e) {
//
//                }
//            });
//            watch.close();
////            String log = podPodResource
////                    .sinceSeconds(1)
////                    .getLog();
////            System.out.println(log);
////            LogWatch logWatch = podPodResource
////                    .usingTimestamps()
////                    .watchLog();
////            Scanner scanner = new Scanner(logWatch.getOutput());
////            while (scanner.hasNextLine()){
////                System.out.println(scanner.nextLine());
////            }
//
//
//        }
    }

//    @Test
    public void delete(){
        String jobName = "test-job";
        KubernetesClient client = kubernetesManager.getClient();
        ScalableResource<Job> train = client.batch().jobs().inNamespace("train").withName(jobName);
        Boolean delete = train.delete();
        System.out.println(delete);

    }

//    @Test
    public void getStatus(){

        String jobName = "test-job";
        KubernetesClient client = kubernetesManager.getClient();
        Job test = client.batch().jobs().inNamespace("train").withName(jobName).get();
        JobStatus status = test.getStatus();
        System.out.println(status);
    }

//    @Test
    public void findNull(){
        KubernetesClient client = kubernetesManager.getClient();
        Job job = client.batch().jobs().inNamespace("train").withName("NULL").get();
        System.out.println(job);
    }
}
