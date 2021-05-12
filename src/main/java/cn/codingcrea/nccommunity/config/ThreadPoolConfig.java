package cn.codingcrea.nccommunity.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
@EnableAsync
//需要这个配置类才能启动spring定时任务
public class ThreadPoolConfig {
}
