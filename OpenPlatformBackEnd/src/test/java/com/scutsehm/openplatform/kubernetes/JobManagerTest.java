package com.scutsehm.openplatform.kubernetes;

import com.scutsehm.openplatform.POJO.entity.PathParameter;
import com.scutsehm.openplatform.POJO.entity.RuntimeParameter;
import com.scutsehm.openplatform.POJO.entity.SpacePath;
import com.scutsehm.openplatform.POJO.enums.FileSpace;
import io.fabric8.kubernetes.api.model.batch.Job;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

//@SpringBootTest
public class JobManagerTest {

    @Autowired
    private JobManager jobManager;

//    @Test
    public void testCrete(){
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
                .jobName("job-test-1")
                .imageName("python")
                .namespace("train")
                .parameterList(Collections.singletonList(times)).build();

        Job job = jobManager.create(config);
        System.out.println(job);
    }

//    @Test
    public void testWatch() throws InterruptedException {
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
                .applicationEntry("test2.py")
                .jobName("job-test-4")
                .imageName("python")
                .namespace("train")
                .parameterList(Collections.singletonList(times)).build();

        Job job = jobManager.create(config);
        jobManager.watchJobStatus(job);
        CountDownLatch latch = new CountDownLatch(1);
        System.out.println("BEGIN WAITING");
        latch.await();
    }
}
