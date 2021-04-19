package cn.codingcrea.nccommunity;

import cn.codingcrea.nccommunity.dao.AlphaDao;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
//下面注解的作用https://www.cnblogs.com/suiy-160428/p/11976940.html
@ContextConfiguration(classes = NccommunityApplication.class)
//继承ApplicationContextAware以获取spring容器
class NccommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Test
	public void testApplicationContext() {
		System.out.println(applicationContext);
		AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);	//@Primary注解的实现
		System.out.println(alphaDao.test());

		alphaDao = applicationContext.getBean("alphaDaoImpl", AlphaDao.class);	//指定实现类
		System.out.println(alphaDao.test());
	}

//	@Test
//	public void testBeanManagement() {
//		AlphaService alphaService = applicationContext.getBean(AlphaService.class);
//		System.out.println(alphaService);
//	}

	@Test
	public void testBeanConfig() {
		SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
		System.out.println(simpleDateFormat.format(new Date()));
	}

}
