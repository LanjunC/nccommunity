package cn.codingcrea.nccommunity;

import cn.codingcrea.nccommunity.dao.DiscussPostMapper;
import cn.codingcrea.nccommunity.dao.LoginTicketMapper;
import cn.codingcrea.nccommunity.dao.MessageMapper;
import cn.codingcrea.nccommunity.dao.UserMapper;
import cn.codingcrea.nccommunity.entity.DiscussPost;
import cn.codingcrea.nccommunity.entity.LoginTicket;
import cn.codingcrea.nccommunity.entity.Message;
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
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MessageMapper messageMapper;

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
        List<DiscussPost> discussPosts = discussPostMapper.selectDiscussPosts(0, 0, 10, 0);
        System.out.println(discussPosts);

        System.out.println(discussPostMapper.selectDiscussPostRows(0));
    }

    @Test
    public void testInsertLoginTicket() {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000 * 60 * 5));   //5分钟

        loginTicketMapper.insertLoginTicket(loginTicket);
    }

    @Test
    public void testSelectAndUpdateLoginTicket() {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus("abc", 1);
        loginTicket = loginTicketMapper.selectByTicket("abc");
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectLetters() {
        List<Message> messages = messageMapper.selectConversations(111, 0, 20);
        for(Message message : messages) {
            System.out.println(message);
        }

        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);

        List<Message> messagesOf111 = messageMapper.selectLetters("111_112", 0, 20);
        for(Message message : messagesOf111) {
            System.out.println(message);
        }

        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);

        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);
    }
}
