package com.community.community.util;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

@Component
public class SensitiveFilter {

    private static final Logger logger = LoggerFactory.getLogger(SensitiveFilter.class);

    private static final String REPLACEMENT = "***";

    //根节点
    private TrieNode rootNode = new TrieNode();

    @PostConstruct
    public void init(){
        try (
                InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream));
            ){
            String keyword;
            while((keyword = reader.readLine())!=null){
                this.addKeyWord(keyword);
            }
        } catch (Exception e) {
            logger.error("加载敏感词文件失败:"+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //将敏感词添加到前缀树中
    public void addKeyWord(String keyWord){
        TrieNode temp = rootNode;
        for(int i = 0;i<keyWord.length();i++){
            char key = keyWord.charAt(i);
            TrieNode subNode = temp.getSubNode(key);
            if(subNode==null){
                subNode = new TrieNode();
                temp.addSubNode(key,subNode);
            }
            temp = subNode;
            //关键词结束标识
            if(i == keyWord.length()-1){
                temp.setKeywordEnd(true);
            }
        }
    }

    /**
     * 过滤铭感词
     * @return
     */
    public String filter(String text){

        if(StringUtils.isBlank(text)){
            return null;
        }
        //树根节点指针
        TrieNode tempNode = rootNode;
        //指针1
        int begin = 0;
        //指针2
        int position = 0;
        //结果
        StringBuilder sb = new StringBuilder();

        while(position<text.length()){
            //当前字符
            char c = text.charAt(position);
            //跳过特殊符号
            if(isSymbol(c)){
                //若指针1在根节点 则指针二走动
                if(tempNode==rootNode){
                    sb.append(c);
                    begin++;
                }
                position++;
                continue;
            }
            //检查下级节点
            tempNode = tempNode.getSubNode(c);
            if(tempNode == null){
                //不是铭感词
                sb.append(text.charAt(begin));
                begin = ++position;
                tempNode = rootNode;
            }else if(tempNode.isKeywordEnd()){
                 sb.append(REPLACEMENT);
                 begin = ++position;
                 tempNode = rootNode;
            }else{
                position++;
            }
        }
        //记录最后一批字符
        sb.append(text.substring(begin));
        return sb.toString();
    }

    private boolean isSymbol(Character character){
        //0x2E80 - 0x8FFF 东亚文字范围
        return !CharUtils.isAsciiAlphanumeric(character) && (character < 0x2E80 || character > 0x8FFF);
    }

    private class TrieNode{
        //关键词结束标识
        private boolean isKeywordEnd = false;
        //子节点列表
        private Map<Character,TrieNode> subNodes = new HashMap<>();

        public void addSubNode(Character key,TrieNode node){
            subNodes.put(key,node);
        }

        public TrieNode getSubNode(Character key){
            return subNodes.get(key);
        }

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }
    }
}
