package cn.codingcrea.nccommunity.controller;

import cn.codingcrea.nccommunity.entity.Comment;
import cn.codingcrea.nccommunity.service.CommentService;
import cn.codingcrea.nccommunity.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    //发布评论后还希望回到当前帖
    @RequestMapping(path = "/add/{discussPostId}", method = RequestMethod.POST)
    public String addomment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        //后面做统一权限管理和异常管理，因此没登录这里不会到达
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
