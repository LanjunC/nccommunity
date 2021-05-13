package cn.codingcrea.nccommunity;

import cn.codingcrea.nccommunity.entity.DiscussPost;
import cn.codingcrea.nccommunity.service.DiscussPostService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NccommunityApplication.class)
public class CaffeineTest {

    @Autowired
    private DiscussPostService postService;

    @Test
    public void initDataForTest() {
        for (int i = 0; i < 300000; i++) {
            DiscussPost post = new DiscussPost();
            post.setUserId(111);
            post.setTitle("插入30w条帖子做测试" + i);
            post.setContent("那么， 那么， 对我个人而言，帖子不仅仅是一个重大的事件，还可能会改变我的人生。 我们都知道，只要有意义，那么就必须慎重考虑。 本人也是经过了深思熟虑，在每个日日夜夜思考这个问题。 既然如何， 那么， 既然如此， 总结的来说， 我们不得不面对一个非常尴尬的事实，那就是， 所谓帖子，关键是帖子需要如何写。 一般来说， 而这些并不是完全重要，更加重要的问题是， 普列姆昌德曾经说过，希望的灯一旦熄灭，生活刹那间变成了一片黑暗。带着这句话, 我们还要更加慎重的审视这个问题。");
            post.setCreateTime(new Date());
            post.setScore(Math.random() * 3000);
            postService.addDiscussPost(post);
        }
    }

    @Test
    public void testCache() {
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 1));
        System.out.println(postService.findDiscussPosts(0, 0, 10, 0));
    }

}