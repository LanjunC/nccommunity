package cn.codingcrea.nccommunity.config;

import cn.codingcrea.nccommunity.quartz.AlphaJob;
import cn.codingcrea.nccommunity.quartz.PostScoreRefreshJob;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

//第一次配置（配置文件）并启动就会导入数据库，此后调用数据库
@Configuration
public class QuartzConfig {

    //BeanFactory是spring顶层容器
    //FactoryBean简化bean的实例化过程
    //1.通过FactoryBean封装Bean的实例化过程
    //2.将FactoryBean装配到容器里
    //3.将FactoryBean注入给其他Bean
    //4.该Bean得到的是FactoryBean管理的实例

    //一旦加载待数据库，服务器启动就会一直执行，想要停止可以QuartzTest中删除服务器数据，并取消这里的注入容器
//    @Bean
    //Bean注入的实例名默认为方法名
    public JobDetailFactoryBean alphaJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(AlphaJob.class);
        factoryBean.setName("alphaJob");
        factoryBean.setGroup("alphaJobGroup");
        factoryBean.setDurability(true);    //任务是否长久保存？暂时不知道意义
        factoryBean.setRequestsRecovery(true);  //任务是否可恢复
        return factoryBean;
    }

    //Simple的和Cron的都可以，后者可以用于复杂Trigger
//    @Bean
    public SimpleTriggerFactoryBean alphaTrigger(JobDetail alphaJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(alphaJobDetail);
        factoryBean.setName("alphaTrigger");
        factoryBean.setGroup("alphaTriggerGroup");
        factoryBean.setRepeatInterval(3000);
        factoryBean.setJobDataMap(new JobDataMap());    //Trigger底层需要存储Job一些状态，这里采用默认类型来存
        return factoryBean;
    }

    //刷新帖子分数任务

    @Bean
    public JobDetailFactoryBean postScoreRefreshJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(PostScoreRefreshJob.class);
        factoryBean.setName("postScoreRefreshJob");
        factoryBean.setGroup("postScoreRefreshJobGroup");
        factoryBean.setDurability(true);    //任务是否长久保存？暂时不知道意义
        factoryBean.setRequestsRecovery(true);  //任务是否可恢复
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean postScoreRefreshTrigger(JobDetail postScoreRefreshJobDetail) {
        SimpleTriggerFactoryBean factoryBean = new SimpleTriggerFactoryBean();
        factoryBean.setJobDetail(postScoreRefreshJobDetail);
        factoryBean.setName("postScoreRefreshTrigger");
        factoryBean.setGroup("postScoreRefreshTriggerGroup");
        factoryBean.setRepeatInterval(1000 * 60 * 5);
        factoryBean.setJobDataMap(new JobDataMap());    //Trigger底层需要存储Job一些状态，这里采用默认类型来存
        return factoryBean;
    }
}
