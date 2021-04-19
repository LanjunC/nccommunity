package cn.codingcrea.nccommunity.service;

import cn.codingcrea.nccommunity.dao.UserMapper;
import cn.codingcrea.nccommunity.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserMapper userMapper;

    public User findUserById(int id) {
        return userMapper.selectById(id);
    }
}
