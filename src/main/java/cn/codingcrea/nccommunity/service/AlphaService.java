package cn.codingcrea.nccommunity.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
//@Scope("prototype")
public class AlphaService {

    private static final Logger logger = LoggerFactory.getLogger(AlphaService.class);

//    public AlphaService() {
//        System.out.println("实例化AlphaService");
//    }
//
//    @PostConstruct  //注解的作用是容器在该bean构造器调用后调用该方法进行初始化
//    public void init() {
//        System.out.println("初始化AlphaService");
//    }
//
//    @PreDestroy     //销毁前调用
//    public void destroy() {
//        System.out.println("销毁AlphaService");
//    }

    @Async
    public void execute1() {
        logger.debug("通过注解方式使用spring线程池");
    }

//    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
//    public void execute2() {
//        logger.debug("通过注解方式使用spring定时任务执行器");
//    }
}
