package com.sean.study;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * 注册服务提供者
 *
 * @Author: sean
 * @Date: 2021/4/2 15:44
 */
@SpringBootApplication
@EnableEurekaClient
public class HelloApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelloApplication.class, args);
    }
}
