package cn.codingcrea.nccommunity.controller.interceptor;

import cn.codingcrea.nccommunity.entity.LoginTicket;
import cn.codingcrea.nccommunity.entity.User;
import cn.codingcrea.nccommunity.service.UserService;
import cn.codingcrea.nccommunity.util.CookieUtil;
import cn.codingcrea.nccommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket = CookieUtil.getValue(request, "ticket");
        if(ticket != null) {
            LoginTicket loginTicket = userService. findLoginTicket(ticket);
            //查询凭证是否有效
            if(loginTicket != null && loginTicket.getStatus() == 0 && loginTicket.getExpired().after(new Date())) {
                //本次请求中持有该用户
                User user = userService.findUserById(loginTicket.getUserId());
                hostHolder.setUser(user);

                //构建用户认证结果，并存入SecurityContext，以便于Security获取
                Authentication authentication = new UsernamePasswordAuthenticationToken(user, user.getPassword(),
                        userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user != null && modelAndView != null) {
            modelAndView.addObject("loginUser", user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if(hostHolder.getUser()==null) {
            SecurityContextHolder.clearContext();
        }
        hostHolder.clear();
    }
}
