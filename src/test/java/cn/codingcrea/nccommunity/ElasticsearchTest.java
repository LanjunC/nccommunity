package cn.codingcrea.nccommunity;

import cn.codingcrea.nccommunity.dao.DiscussPostMapper;
import cn.codingcrea.nccommunity.dao.elasticsearch.DiscussPostRepository;
import cn.codingcrea.nccommunity.entity.DiscussPost;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = NccommunityApplication.class)
public class ElasticsearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testInsertList() {
        for(int uid = 0; uid < 160; uid++) {
            discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(uid, 0, 200, 0));
        }
    }

    @Test
    public void testUpdate() {
        //??????save??????
    }

    @Test
    public void testDelete(){
//        discussPostRepository.deleteById(231);
    }

    @Test
    public void testSearchByRepository() {
         NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("???????????????", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

//         elasticsearchRestTemplate.queryForPage(searchQuery,class,searchResultMapper);
//        discussPostRepository?????????????????????????????????????????????????????????
//        ???????????????ElasticsearchRestTemplate
        Page<DiscussPost> page = discussPostRepository.search(nativeSearchQuery);
        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for(DiscussPost discussPost : page) {
            System.out.println(discussPost);
        }
    }

    //https://www.cnblogs.com/hi3254014978/p/14055771.html
    @Test
    public void testSearchByTemplate() {
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("???????????????", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(0, 10))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> search = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        // ?????????????????????????????????
        List<SearchHit<DiscussPost>> searchHits = search.getSearchHits();
        // ??????????????????????????????????????????
        List<DiscussPost> discussPosts = new ArrayList<>();
        for(SearchHit<DiscussPost> searchHit : searchHits){
            // ???????????????
            Map<String, List<String>> highLightFields = searchHit.getHighlightFields();
            // ???????????????????????????content???
            searchHit.getContent().setTitle(highLightFields.get("title") == null ? searchHit.getContent().getTitle() : highLightFields.get("title").get(0));
            searchHit.getContent().setTitle(highLightFields.get("content") == null ? searchHit.getContent().getContent() : highLightFields.get("content").get(0));
            // ??????????????????
            discussPosts.add(searchHit.getContent());
        }
        System.out.println(discussPosts.size());
        for(DiscussPost discussPost : discussPosts){
            System.out.println(discussPost);
        }

    }
}
