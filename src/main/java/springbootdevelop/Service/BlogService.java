package springbootdevelop.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import springbootdevelop.domain.Articles;
import springbootdevelop.dto.AddArticleRequest;
import springbootdevelop.dto.UpdateArticleRequest;
import springbootdevelop.repository.BlogRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BlogService {
    private final BlogRepository blogRepository;

    //블로그 글 추가 메서드
    public Articles save(AddArticleRequest request){
        return blogRepository.save(request.toEntity());
    }

    //JPA 지원 메서드인 findAll()을 호출해 Articles 테이블에 저장된 모든 데이터를 조회
    public List<Articles> findAll(){
        return blogRepository.findAll();
    }

    //삭제 api
    public void delete(Long id){
        blogRepository.deleteById(id);
    }

    //글 조회 api
    public Articles findById(long id){
        return blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    //글 수정 api
    @Transactional
    public Articles update(long id, UpdateArticleRequest request){
        Articles articles = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        articles.update(request.getTitle(), request.getContent());

        return articles;
    }
}
