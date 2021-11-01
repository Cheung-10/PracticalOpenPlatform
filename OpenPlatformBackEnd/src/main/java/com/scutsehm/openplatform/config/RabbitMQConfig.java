package com.scutsehm.openplatform.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ相关配置
 */
@Configuration
public class RabbitMQConfig {
    /**
     * TrainModelTask 相关 的消息队列
     * @return Queue
     */
    @Bean
    public Queue trainModelTaskQueue(){
        return new Queue("TrainModelTaskQueue", true);
    }


    /**
     * TrainModelTask 消息交换机
     * @return Exchange
     */
    @Bean
    FanoutExchange trainModelTaskExchange() {
        return new FanoutExchange("TrainModelTaskExchange",true,false);
    }

    /**
     * 绑定
     * @return
     */
    @Bean
    Binding trainModelTaskBinding() {
        return BindingBuilder.bind(trainModelTaskQueue()).to(trainModelTaskExchange());
    }



    /**
     * ProcessModelTask 相关 的消息队列
     * @return Queue
     */
    @Bean
    public Queue processModelTaskQueue(){
        return new Queue("ProcessModelTaskQueue", true);
    }


    /**
     * ProcessModelTask 消息交换机
     * @return Exchange
     */
    @Bean
    FanoutExchange processModelTaskExchange() {
        return new FanoutExchange("ProcessModelTaskExchange",true,false);
    }

    /**
     * 绑定
     * @return
     */
    @Bean
    Binding processModelTaskBinding() {
        return BindingBuilder.bind(processModelTaskQueue()).to(processModelTaskExchange());
    }
}
