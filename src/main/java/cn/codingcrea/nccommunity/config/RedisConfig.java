package cn.codingcrea.nccommunity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    //我们一般用String为key的就够用了
    //RedisConnectionFactory factory是已经被实例化的bean，可以通过参数导入
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //设置key的序列化方式,使用一个String的序列化器
        template.setKeySerializer(RedisSerializer.string());

        //设置value的序列化方式，使用一个json的序列化器
        template.setValueSerializer(RedisSerializer.json());

        //设置hash的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());

        //设置hash的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //使上面的生效
        template.afterPropertiesSet();
        return template;
    }
}
