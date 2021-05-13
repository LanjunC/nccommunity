package cn.codingcrea.nccommunity;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class NccommunityServletInitializer extends SpringBootServletInitializer {

    //程序入口。主配置文件来自NccommunityApplication.class
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(NccommunityApplication.class);
    }
}
