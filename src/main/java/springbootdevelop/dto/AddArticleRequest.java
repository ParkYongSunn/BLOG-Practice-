package springbootdevelop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import springbootdevelop.domain.Articles;

@NoArgsConstructor
@AllArgsConstructor // 모든 필드 값을 파라미터로 받는 생성자 추가
@Getter
public class AddArticleRequest {
    private String title;
    private String content;

    public Articles toEntity(){
        return Articles.builder().title(title).content(content).build();
    }
}
