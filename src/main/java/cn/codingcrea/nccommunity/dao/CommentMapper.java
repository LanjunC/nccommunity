package cn.codingcrea.nccommunity.dao;

import cn.codingcrea.nccommunity.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    List<Comment> selectCommentByEntity(int entityType, int entityId, int offset, int limit);
    int selectCountByEntity(int entityType, int entityId);
    int insertComment(Comment comment);
}
