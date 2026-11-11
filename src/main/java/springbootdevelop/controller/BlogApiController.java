package springbootdevelop.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springbootdevelop.Service.BlogService;
import springbootdevelop.domain.Articles;
import springbootdevelop.dto.AddArticleRequest;
import springbootdevelop.dto.ArticleResponse;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class BlogApiController {
    private final BlogService blogService;

    //저장 api
    @PostMapping("/api/articles")
    public ResponseEntity<Articles> addArticle(@RequestBody AddArticleRequest request){
        Articles savedArticle = blogService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);

    }

    //글 목록 조회 api
    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles = blogService.findAll().stream().map(ArticleResponse::new).toList();

        return ResponseEntity.ok().body(articles);
    }

    //글 조회 api
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){
        Articles articles = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(articles));
    }

    //삭제 api
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticles(@PathVariable long id){
        blogService.delete(id);

        return ResponseEntity.ok().build();
    }
}
