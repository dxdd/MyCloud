package com.sean.study.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * @Author: sean
 * @Date: 2021/4/2 15:44
 */
@RestController
public class HelloController {
    private Logger logger = Logger.getLogger(String.valueOf(getClass()));
    @Autowired
    private DiscoveryClient client;

    @Qualifier("eurekaRegistration")
    @Autowired
    private Registration registration;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String index() {

        try {
//            ServiceInstance instance = serviceInstance();
            int sleepTime = new Random().nextInt(3000);
//            logger.info(instance.getInstanceId() + instance.getPort());
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Hello World";
    }

    public ServiceInstance serviceInstance() {
        List<ServiceInstance> list = client.getInstances(registration.getServiceId());
        if (list != null && list.size() > 0) {
            for (ServiceInstance itm : list) {
                if (itm.getPort() == 8080) {
                    logger.info(String.valueOf(itm.getPort()));
                    return itm;
                }
            }
        }
        return null;
    }
}
