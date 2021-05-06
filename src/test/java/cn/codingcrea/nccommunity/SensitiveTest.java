package cn.codingcrea.nccommunity;

import cn.codingcrea.nccommunity.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NccommunityApplication.class)
public class SensitiveTest {

    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter1() {
        String text = "这里可以赌博，可以嫖娼，可以卖淫，可以吸毒";
        System.out.println(text);
        System.out.println(sensitiveFilter.filter(text));
    }

    @Test
    public void testSensitiveFilter2() {
        String text = "这里可以◇赌◇博◇，可以嫖◇娼◇，可以◇卖◇淫，可以◇吸毒◇";
        System.out.println(text);
        System.out.println(sensitiveFilter.filter(text));
    }
}
