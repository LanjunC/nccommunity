package cn.codingcrea.nccommunity;

import cn.codingcrea.nccommunity.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NccommunityApplication.class)
public class ThreadPoolTest {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolTest.class);

    //JDK普通线程池
    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    //JDK可执行定时任务的线程池
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);

    //spring普通线程池
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    //spring可执行定时任务的线程池
    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private AlphaService alphaService;


    //主线程结束后会等待其他线程结束进程才退出
    //但为啥用线程池就不会等待？？
    private void sleep(long m) {
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testExecutorService() throws InterruptedException {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                sleep(1000);
                logger.debug("hello executorService");

            }
        };

        for(int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        sleep(2000);
    }

    @Test
    public void testScheduledExecutorService() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello scheduledExecutorService");
            }
        };

        scheduledExecutorService.scheduleAtFixedRate(task, 5000, 1000, TimeUnit.MILLISECONDS);

        sleep(20000);
    }

    @Test
    public void testThreadPoolTaskExecutor() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello threadPoolTaskExecutor");
            }
        };

        for(int i = 0; i < 10; i++) {
            executorService.submit(task);
        }

        sleep(2000);
    }

    @Test
    public void testThreadPoolTaskScheduler() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                logger.debug("hello threadPoolTaskScheduler");
            }
        };

        Date date = new Date(System.currentTimeMillis() + 5000);
        threadPoolTaskScheduler.scheduleAtFixedRate(task, date, 1000);

        sleep(20000);
    }

    //spring支持注解的方式，见AlphaService
    @Test
    public void testThreadPoolTaskExecutorSimple() {
        for(int i = 0; i < 10; i++) {
            alphaService.execute1();
        }

        sleep(2000);
    }

    //spring支持注解的方式，见AlphaService
    @Test
    public void testThreadPoolTaskSchedulerSimple() {
        sleep(10000);
    }
}
