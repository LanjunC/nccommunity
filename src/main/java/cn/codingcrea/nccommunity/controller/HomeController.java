package cn.codingcrea.nccommunity.controller;

import cn.codingcrea.nccommunity.entity.DiscussPost;
import cn.codingcrea.nccommunity.entity.Page;
import cn.codingcrea.nccommunity.service.DiscussPostService;
import cn.codingcrea.nccommunity.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String getIndexPage(Model model, Page page) {
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index");

        List<DiscussPost> list = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit());
        List<Map<String,Object>> discussPosts = new ArrayList<>();
        if(list != null) {
            for(DiscussPost discussPost : list) {
                Map<String,Object> map =  new HashMap<>();
                map.put("post", discussPost);
                map.put("user", userService.findUserById(discussPost.getUserId())); //这里也可以在map层做级联查询调出user数据
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        //model加page可以省略
        return "/index";
    }

    @RequestMapping(path = "/error", method = RequestMethod.GET)
    public String getErrorPage() {
        return "/error/500";
    }
}
