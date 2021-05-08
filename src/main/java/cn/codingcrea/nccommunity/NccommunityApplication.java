package cn.codingcrea.nccommunity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

//标识这是springboot配置文件，点进去可以看源码
//springboot自动配置原理https://zhuanlan.zhihu.com/p/102942848
@SpringBootApplication
public class NccommunityApplication {

	@PostConstruct
	public void init() {
		//解决netty启动冲突的问题（redis和es冲突）
		System.setProperty("es.set.netty.runtime.available.processors", "false");
	}

	public static void main(String[] args) {
		SpringApplication.run(NccommunityApplication.class, args);
	}

}
