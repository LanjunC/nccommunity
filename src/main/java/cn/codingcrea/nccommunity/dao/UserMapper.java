package cn.codingcrea.nccommunity.dao;

import cn.codingcrea.nccommunity.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    User selectById(int id);

    User selectByUsername(String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateHeaderUrl(int id, String headerUrl);

    int updatePassword(int id, String password);

}
