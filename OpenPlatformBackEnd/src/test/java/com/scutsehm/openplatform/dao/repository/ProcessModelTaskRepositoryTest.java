package com.scutsehm.openplatform.dao.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProcessModelTaskRepositoryTest {

    @Autowired
    private ProcessModelTaskRepository processModelTaskRepository;

//    @Test
    public void test(){
        String publisherById = processModelTaskRepository.findPublisherById("process-1fed80bc-c47e-46ab-86a4-20c4fcbe6c40");
        System.out.println(publisherById);
    }
}