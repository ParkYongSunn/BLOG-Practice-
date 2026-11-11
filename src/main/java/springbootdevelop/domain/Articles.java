package springbootdevelop.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Articles {

    //엔티티 ID > BIGING > PK > NOT NULL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //기본키를 자동으로 +1씩 증가
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    //엔티티 TITLE > VARCHAR > NOT NULL
    @Column(name = "title", nullable = false)
    private String title;

    //엔티티 CONTENT > VARCHAR > NOT NULL
    @Column(name = "content", nullable = false)
    private String content;

    //빌더 패턴으로 객체 생성
    @Builder
    public Articles(String title, String content){
        this.title = title;
        this.content = content;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

}
