package com.scutsehm.openplatform.dao.repository;

import com.scutsehm.openplatform.POJO.entity.TaskLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


//@SpringBootTest
class TaskLogRepositoryTest {
    @Autowired
    private TaskLogRepository taskLogRepository;

//    @Test
    public void test(){
        TaskLog test1 = new TaskLog("1", "test1");
        TaskLog test2 = new TaskLog("1", "test2");
        taskLogRepository.save(test1);
        taskLogRepository.save(test2);
    }
}