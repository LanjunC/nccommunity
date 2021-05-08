package cn.codingcrea.nccommunity.dao.elasticsearch;

import cn.codingcrea.nccommunity.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
