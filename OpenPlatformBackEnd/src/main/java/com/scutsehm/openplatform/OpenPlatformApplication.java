package com.scutsehm.openplatform;


import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
@MapperScan(basePackages = "com.scutsehm.openplatform.dao.mapper")
//@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class OpenPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenPlatformApplication.class, args);
    }

}

//暂时关闭SpringSecurity的方法：
//1. 在SpringBootApplication中添加exclude = { SecurityAutoConfiguration.class }
//2. 将SpringSecurity包中SecurityConfig文件中的@Configuration、@EnableWebSecurity注解 注释掉
