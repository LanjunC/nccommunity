package cn.codingcrea.nccommunity.dao;

import cn.codingcrea.nccommunity.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户会话列表，每个列表只返回一条最新的私信
    List<Message> selectConversations(int userId, int offset, int limit);

    //查询当前用户会话数量
    int selectConversationCount(int userId);

    //查询某个会话包含的私信列表
    List<Message> selectLetters(String conversationId,int offset, int limit);

    //查询某个会话包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读私信数量
    int selectLetterUnreadCount(int userId,String conversationId);

    //添加私信
    int insertMessage(Message message);

    //修改私信的（已读）状态
    int updateStatus(List<Integer> ids, int status);

}
