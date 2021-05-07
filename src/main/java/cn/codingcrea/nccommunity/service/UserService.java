package cn.codingcrea.nccommunity.service;

import cn.codingcrea.nccommunity.dao.LoginTicketMapper;
import cn.codingcrea.nccommunity.dao.UserMapper;
import cn.codingcrea.nccommunity.entity.LoginTicket;
import cn.codingcrea.nccommunity.entity.User;
import cn.codingcrea.nccommunity.util.CommunityConstant;
import cn.codingcrea.nccommunity.util.MailClient;
import cn.codingcrea.nccommunity.util.NcCommunityUtil;
import cn.codingcrea.nccommunity.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {

    @Autowired
    private UserMapper userMapper;

//    @Autowired
//    private LoginTicketMapper loginTicketMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${nccommunity.path.domain}")
    private String domain;

    //项目路径
    @Value("${server.servlet.context-path}")
    private String contextPath;

    public User findUserById(int id) {
//        return userMapper.selectById(id);
        User user = getCache(id);
        if(user == null) {
            user = initCache(id);
        }
        return user;

    }

    public Map<String,Object> register(User user) {
        Map<String,Object> map = new HashMap<>();
        if(user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if(StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg","账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg","密码不能为空！");
            return map;
        }
        if(StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "邮箱不能为空！");
            return map;
        }

        //验证账号
        User u = userMapper.selectByUsername(user.getUsername());
        if(u != null) {
            map.put("usernameMsg","该账号已存在！");
            return map;
        }

        //验证邮箱
        u = userMapper.selectByEmail(user.getEmail());
        if(u != null) {
            map.put("emailMsg","该邮箱已存在！");
            return map;
        }

        //注册
        user.setSalt(NcCommunityUtil.generateUUID().substring(0,5));
        user.setPassword(NcCommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);    //普通用户
        user.setStatus(0);  //还未激活
        user.setActivationCode(NcCommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        context.setVariable("url", domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode());
        String content  = templateEngine.process("/mail/activation", context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mailClient.sendMail(user.getEmail(), "激活账号", content);
            }
        }).start(); //由于发送邮件太慢，直接交给多线程去处理


        return map;
    }

    public int activation(int userId, String code) {
        User user = userMapper.selectById(userId);
        if(user.getStatus()== 1) {
            return ACTIVATION_REPEATE;
        } else if(user.getActivationCode().equals(code)) {
            userMapper.updateStatus(userId, 1);
            clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }
    }

    public Map<String,Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        //空值
        if(StringUtils.isBlank(username)) {
            map.put("usernameMsg", "账号不能为空！");
            return map;
        }
        if(StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空！");
            return map;
        }

        //验证
        User user = userMapper.selectByUsername(username);
        if(user == null) {
            map.put("usernameMsg", "该账号不存在！");
            return map;
        }
        if(user.getStatus() == 0) {
            map.put("usernameMsg", "该账号尚未激活！");
            return map;
        }
        password = NcCommunityUtil.md5(password+user.getSalt());
        if(password.equals(user.getPassword())) {
            map.put("passwordMsg", "密码错误！");
            return map;
        }

        //生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(NcCommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() +  expiredSeconds * 1000));
//        loginTicketMapper.insertLoginTicket(loginTicket);
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(redisKey,loginTicket);

        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public void logout(String ticket) {
//        loginTicketMapper.updateStatus(ticket, 1);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket)redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey, loginTicket);

    }

    public LoginTicket findLoginTicket(String ticket) {
//        return loginTicketMapper.selectByTicket(ticket);
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int userId, String headerUrl) {
        int i = userMapper.updateHeaderUrl(userId, headerUrl);
        clearCache(userId);
        return i;
    }

    public User findUserByName(String username) {
        return userMapper.selectByUsername(username);
    }

    //1.优先从缓存中取值
    private User getCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        return (User)redisTemplate.opsForValue().get(redisKey);
    }

    //2.取不到时初始化缓存数据
    private User initCache(int userId) {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(redisKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    //3.数据变更时清除缓存信息
    private void clearCache(int userId) {
        String redisKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(redisKey);
    }

    //redis、数据库不一致问题https://developer.aliyun.com/article/712285
    //由于该项目涉及到的主要是user信息，所以很难涉及到对同一行的并发访问，可以不采用延时双删等策略
    //重试机制？
    //这篇比较完善，以后再看：https://www.cnblogs.com/dingpeng9055/p/11562261.html
}
