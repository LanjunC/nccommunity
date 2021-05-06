package cn.codingcrea.nccommunity.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    //替换符号
    private static final String REPLACEMENT = "**";

    //根节点
    private  TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init() {
        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");//classes类路径
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        ) {
            String keyword;
            while((keyword = reader.readLine()) != null) {
                this.addKeyword(keyword);
            }
        }catch(IOException e) {
            logger.error("加载敏感词文件失败:" + e.getMessage());
        }
    }

    /**
     * 用于过滤文本的敏感词，被外界所调用
     * @param text 待过滤文本
     * @return 过滤后的文本
     */
    public String filter(String text) {
        if(StringUtils.isBlank(text)) {
            return null;
        }

        //指针1，指向树
        TrieNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int position = 0;

        StringBuilder sb = new StringBuilder();

        while(position < text.length()) {
            char c = text.charAt(position);

            //跳过符号
            if(isSymbol(c)) {
                //若指针1位于根节点（即position == ），说明过滤判断还没开始，因此该符号可以计入结果
                if(tempNode == rootNode) {
                    sb.append(c);
                    begin++;
                }
                //position始终跳过该符号
                position++;
                continue;
            }
            tempNode = tempNode.getSubnode(c);
            //以begin开头的子字符串不是敏感词
            if(tempNode == null) {
                sb.append(c);
                position = ++begin;
                tempNode = rootNode;
            } else if(tempNode.isKeywordEnd()) {
                //发现敏感词
                sb.append(REPLACEMENT);
                begin = ++position;
                tempNode = rootNode;
            } else {
                //继续检查
                position++;
            }
        }
        //最后的几个字符计入
        sb.append(text.substring(begin));
        return sb.toString();
    }

    private class TrieNode {

        //关键词结束标识
        private boolean isKeywordEnd = false;

        //子节点
        private Map<Character, TrieNode> subnodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        //添加子节点操作
        public void addSubnode(Character c, TrieNode node) {
            subnodes.put(c, node);
        }
        //获取子节点操作
        public TrieNode getSubnode(Character c) {
            return subnodes.get(c);
        }
    }


    private void addKeyword(String keyword) {
        TrieNode trieNode = rootNode;
        for(int i = 0; i < keyword.length(); i++){
            char c = keyword.charAt(i);
            TrieNode subnode = trieNode.getSubnode(c);
            if(subnode == null) {
                subnode = new TrieNode();
                trieNode.addSubnode(c, subnode);
            }
            trieNode = subnode;
        }
        //最后别忘了设置结束标志
        trieNode.setKeywordEnd(true);
    }

    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF); //0x2E80到0x9FFF为东亚文字
    }

}
