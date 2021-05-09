package cn.codingcrea.nccommunity.event;


import cn.codingcrea.nccommunity.entity.DiscussPost;
import cn.codingcrea.nccommunity.entity.Event;
import cn.codingcrea.nccommunity.entity.Message;
import cn.codingcrea.nccommunity.service.DiscussPostService;
import cn.codingcrea.nccommunity.service.ElasticSearchService;
import cn.codingcrea.nccommunity.service.MessageService;
import cn.codingcrea.nccommunity.util.CommunityConstant;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) {
            logger.error("消息的格式不对！");
            return;
        }

        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId",event.getEntityId());
        if(!event.getData().isEmpty()) {
            for(Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }

    //帖子有增加或更改，es服务器要增添或更改
    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublishMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) {
            logger.error("消息的格式不对！");
            return;
        }

        DiscussPost post = discussPostService.findDiscussPostById(event.getEntityId());
        elasticSearchService.saveDiscussPost(post);
    }

    //删帖
    @KafkaListener(topics = {TOPIC_DELETE})
    public void handleDeleteMessage(ConsumerRecord record) {
        if(record == null || record.value() == null) {
            logger.error("消息为空！");
            return;
        }

        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if(event == null) {
            logger.error("消息的格式不对！");
            return;
        }

        elasticSearchService.deleteDiscussPost(event.getEntityId());
    }
}
