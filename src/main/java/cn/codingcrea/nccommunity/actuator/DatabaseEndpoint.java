package cn.codingcrea.nccommunity.actuator;

import cn.codingcrea.nccommunity.util.NcCommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


@Component
@Endpoint(id = "datasource")
public class DatabaseEndpoint {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseEndpoint.class);

    @Autowired
    private DataSource dataSource;

    /**
     * 利用获取数据库连接情况示例来演示actuator自定义端点用法
     * @return
     */
    @ReadOperation
    public String CheckConnection() {
        try(
                Connection connection = dataSource.getConnection();
                ) {
            return NcCommunityUtil.getJSONString(0, "获取连接成功！");
        } catch (SQLException e) {
            logger.error("获取连接失败:" + e.getMessage());
            return NcCommunityUtil.getJSONString(1, "获取连接失败！");
        }
    }
}
