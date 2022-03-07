package cn.locusc.spring.boot.rjmt.data.mapper;


import cn.locusc.spring.boot.rjmt.data.pojo.Article;

public interface ArticleMapper {

    //根据id查询对应的文章
    public Article selectArticle(Integer id);

}
