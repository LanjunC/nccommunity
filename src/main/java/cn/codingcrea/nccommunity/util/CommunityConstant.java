package cn.codingcrea.nccommunity.util;

public interface CommunityConstant {

    //激活成功
    int ACTIVATION_SUCCESS = 0;

    //重复激活
    int ACTIVATION_REPEATE = 1;

    //激活失败
    int ACTIVATION_FAILURE = 2;

    //默认凭证超时时间
    int DEFAULT_EXPIRED_SECONDS = 3600 * 12;

    //“记住我”时的凭证超时时间
    int REMEMBERME_EXPIRED_SECONDS = 3600 * 24 * 100;

    //实体类型为帖子
    int ENTITY_TYPE_POST = 1;

    //实体类型为为comment
    int ENTITY_TYPE_COMMENT = 2;

    //实体类型为用户
    int ENTITY_TYPE_USER = 3;

    /**
     * topic：评论
     */
    String TOPIC_COMMENT = "comment";

    /**
     * topic：点赞
     */
    String TOPIC_LIKE = "like";

    /**
     * topic:关注
     */
    String TOPIC_FOLLOW = "follow";

    /**
     * topic:发帖
     */
    String TOPIC_PUBLISH = "publish";

    /**
     * topic:删帖
     */
    String TOPIC_DELETE = "delete";

    /**
     * 系统在消息系统中使用的用户ID
     */
    int SYSTEM_USER_ID = 1;

    /**
     * 权限:普通用户
     */
    String AUTHORITY_USER = "user";

    /**
     * 权限:管理员
     */
    String AUTHORITY_ADMIN = "admin";

    /**
     * 权限:版主
     */
    String AUTHORITY_MODERATOR = "moderator";
}
