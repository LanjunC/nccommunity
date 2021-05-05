package cn.codingcrea.nccommunity.util;

import cn.codingcrea.nccommunity.entity.User;
import org.springframework.stereotype.Component;

/**
 * 用户容器，用于代替session
 */
@Component
public class HostHolder {

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user) {
        users.set(user);
    }

    public User getUser() {
        return users.get();
    }

    public void clear() {
        users.remove();
    }
}
