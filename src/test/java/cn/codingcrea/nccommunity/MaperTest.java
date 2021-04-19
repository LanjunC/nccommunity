package cn.codingcrea.nccommunity;

import cn.codingcrea.nccommunity.dao.DiscussPostMapper;
import cn.codingcrea.nccommunity.dao.UserMapper;
import cn.codingcrea.nccommunity.entity.DiscussPost;
import cn.codingcrea.nccommunity.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NccommunityApplication.class)
public class MaperTest {

    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @Test
    public void testSelectUser() {
        User user = userMapper.selectById(101);
        System.out.println(user);
        user = userMapper.selectByEmail("nowcoder11@sina.com");
        System.out.println(user);
        user = userMapper.selectByUsername("SYSTEM");
        System.out.println(user);
    }

    @Test
    public void testInsertUser() {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        user.setSalt("abc");
        user.setEmail("213@qq.com");
        user.setType(0);
        user.setStatus(1);
        user.setHeaderUrl("http://www.nowcoder.com/101.png");
        user.setCreateTime(new Date());

        int rows = userMapper.insertUser(user);
        System.out.println(rows);
        System.out.println(user.getId());   //id自动生成了
    }

    @Test
    public void testUpdateUser(){
        System.out.println(userMapper.updateStatus(150, 0));
        System.out.println(userMapper.selectById(150));

        System.out.println(userMapper.updatePassword(150, "123123"));
        System.out.println(userMapper.selectById(150));

        System.out.println(userMapper.updateHeaderUrl(150, "http://www.nowcoder.com/100.png"));
        System.out.println(userMapper.selectById(150));
    }

    @Test
    public void testSelectPosts() {
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10);
        System.out.println(discussPosts);

        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }
}
