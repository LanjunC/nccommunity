package cn.codingcrea.nccommunity.event;

import cn.codingcrea.nccommunity.entity.Event;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate kafkaTemplate;

    //处理事件
    public void fireEvent(Event event) {
        //发布消息到指定topic
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
