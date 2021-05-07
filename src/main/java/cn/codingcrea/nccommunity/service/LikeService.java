package cn.codingcrea.nccommunity.service;

import cn.codingcrea.nccommunity.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeService {

    @Autowired
    private RedisTemplate redisTemplate;

    //点赞
    //entityUserId即被点赞的实体的作者，本来可以通过entityId找，但点赞操作本来是redis操作，这样查会调数据库拉低性能，因此干脆直接作为参数传进来
    public void like(int userId, int entityType, int entityId, int entityUserId) {
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        if(isMember) {
//            redisTemplate.opsForSet().remove(entityLikeKey, userId);
//        } else {
//            redisTemplate.opsForSet().add(entityLikeKey, userId);
//        }

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                Boolean isMember = redisOperations.opsForSet().isMember(entityLikeKey, userId);

                redisOperations.multi();

                if(isMember) {
                    redisOperations.opsForSet().remove(entityLikeKey, userId);
                    redisOperations.opsForValue().decrement(userLikeKey);
                } else {
                    redisOperations.opsForSet().add(entityLikeKey, userId);
                    redisOperations.opsForValue().increment(userLikeKey);
                }

                return redisOperations.exec();
            }
        });
    }

    //查询某实体点赞数量
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态
    //返回int，便于扩展，如踩
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        return isMember ? 1 : 0;
    }

    //查询某个用户获得的赞
    public int findUserLikeCount(int userId) {
        String userLikeKey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer)redisTemplate.opsForValue().get(userLikeKey);
        return count == null ? 0 : count;
    }
}
