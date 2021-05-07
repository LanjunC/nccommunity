package cn.codingcrea.nccommunity;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NccommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testStrings() {
        String redisKey = "test:count";

        redisTemplate.opsForValue().set(redisKey, 1);
        System.out.println(redisTemplate.opsForValue().get(redisKey));
        System.out.println(redisTemplate.opsForValue().decrement(redisKey));
        System.out.println(redisTemplate.opsForValue().increment(redisKey));
    }

    @Test
    public void testHashes() {
    }

    //...

    //多次访问同一key
    @Test
    public void testBoundOperations() {
        String redisKey = "test:count";
        BoundValueOperations operations = redisTemplate.boundValueOps(redisKey);
        System.out.println(operations.get());
    }

    //redis的编程式事务
    @Test
    public void testTransactional() {

        //可以返回自己想要的数据
        Object obj = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String redisKey = "test:tx";

                redisOperations.multi();

                redisOperations.opsForSet().add("test:tx", "nihao");
                redisOperations.opsForSet().add("test:tx", "hehe");
                redisOperations.opsForSet().add("test:tx", "hi");

                //这个结果的返回无论如何都为空（不管之前有没有数据），因此redis不要在事务内做查询，这是无效的
                System.out.println(redisOperations.opsForSet().members("test:tx"));

                return redisOperations.exec();
            }
        });

        System.out.println(obj);
    }


}
