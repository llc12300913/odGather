package com.odgather.po;

import java.util.List;

/**
 * Created by Administrator on 2018/5/17.
 */
public class NewsMessage extends BaseMessage {
    private Integer ArticleCount;
    private List<News> Articles;

    public Integer getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(Integer articleCount) {
        ArticleCount = articleCount;
    }

    public List<News> getArticles() {
        return Articles;
    }

    public void setArticles(List<News> articles) {
        Articles = articles;
    }
}

