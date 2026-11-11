package springbootdevelop.dto;

import lombok.Getter;
import springbootdevelop.domain.Articles;

@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    public ArticleResponse(Articles articles){
        this.title = articles.getTitle();
        this.content = articles.getContent();
    }
}
