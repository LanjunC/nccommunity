package cn.codingcrea.nccommunity.controller;

import cn.codingcrea.nccommunity.entity.Comment;
import cn.codingcrea.nccommunity.entity.DiscussPost;
import cn.codingcrea.nccommunity.entity.Page;
import cn.codingcrea.nccommunity.entity.User;
import cn.codingcrea.nccommunity.service.CommentService;
import cn.codingcrea.nccommunity.service.DiscussPostService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @RequestMapping(path = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title, String content) {
        User user = hostHolder.getUser();
        if(user == null) {
            return NcCommunityUtil.getJSONString(403, "你还没有登录!");
        }

        System.out.println(title);

        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        //type和status默认0
        discussPostService.addDiscussPost(discussPost);

        return NcCommunityUtil.getJSONString(0, "发布成功！");
    }

    @RequestMapping(path = "/detail/{discussPostId}",method = RequestMethod.GET)
    public String getDiscussPost(@PathVariable("discussPostId") int dicussPostId, Model model, Page page) {
        //帖子信息
        DiscussPost post = discussPostService.findDiscussPostById(dicussPostId);
        model.addAttribute("post", post);
        //对于作者信息，可以使用下面的多次查询，也可以在mybatis处进行联合查询（使用mybatis对联合查询的支持）
        User user = userService.findUserById(post.getUserId());
        model.addAttribute("user", user);
        //评论信息
        page.setLimit(5);
        page.setRows(post.getCommentCount());   //冗余数据！
        page.setPath("/discuss/detail/" + dicussPostId);

        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST, post.getId(),
                page.getOffset(), page.getLimit());

        //评论：给帖子的评论
        //回复：给评论的评论
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if(commentList != null) {
            for(Comment comment : commentList) {
                //评论Vo
                Map<String, Object> commentVo = new HashMap<>();
                //评论
                commentVo.put("comment",comment);
                //评论的作者
                commentVo.put("user",userService.findUserById(comment.getUserId()));

                //回复列表
                List<Comment> replyList = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT, comment.getId(),
                        0, Integer.MAX_VALUE);
                //回复Vo列表
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if(replyList != null) {
                    for(Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>();
                        //回复
                        replyVo.put("reply", reply);
                        //作者
                        replyVo.put("user",userService.findUserById(reply.getUserId()));
                        //回复目标，可能是普通回复无目标，也可能有目标
                        User target = reply.getTargetId() == 0 ? null : userService.findUserById(reply.getTargetId());
                        replyVo.put("target", target);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                //回复数量
                int replyCount = commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount",replyCount);

                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }

}
