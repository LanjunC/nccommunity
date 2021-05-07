package cn.codingcrea.nccommunity.controller;


import cn.codingcrea.nccommunity.entity.Page;
import cn.codingcrea.nccommunity.entity.User;
import cn.codingcrea.nccommunity.service.FollowService;
import cn.codingcrea.nccommunity.service.UserService;
import cn.codingcrea.nccommunity.util.CommunityConstant;
import cn.codingcrea.nccommunity.util.HostHolder;
import cn.codingcrea.nccommunity.util.NcCommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @RequestMapping(path = "/follow", method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.follow(user.getId(), entityType, entityId);

        return NcCommunityUtil.getJSONString(0, "已关注!");
    }

    @RequestMapping(path = "/unfollow", method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType, int entityId) {
        User user = hostHolder.getUser();

        followService.unfollow(user.getId(), entityType, entityId);

        return NcCommunityUtil.getJSONString(0, "已取消关注!");
    }

    @RequestMapping(path = "/followees/{userId}", method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user =  userService.findUserById(userId);
        if(user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        //传这个用户仅仅是为了显示他的姓名“xxx关注的人”
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int)followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String,Object>> userList = followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if(userList != null) {
            for(Map<String,Object> map : userList) {
                User u = (User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/followee";
    }

    @RequestMapping(path = "/followers/{userId}", method = RequestMethod.GET)
    public String getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user =  userService.findUserById(userId);
        if(user == null) {
            throw new RuntimeException("该用户不存在！");
        }
        //传这个用户仅仅是为了显示他的姓名“xxx关注的人”
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int)followService.findFolloweeCount(ENTITY_TYPE_USER, userId));

        List<Map<String,Object>> userList = followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if(userList != null) {
            for(Map<String,Object> map : userList) {
                User u = (User)map.get("user");
                map.put("hasFollowed",hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);

        return "/site/follower";
    }

    private boolean hasFollowed(int userId) {
        if(hostHolder.getUser() == null) {
            return false;
        }

        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }
}
