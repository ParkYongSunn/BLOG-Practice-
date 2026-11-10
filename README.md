# BLOG-Practice-
BLOG (Practice)

## Articles 엔티티

`springbootdevelop.domain.Articles` — 블로그 게시글을 나타내는 JPA 엔티티입니다.

### 필드

| 필드 | 타입 | 컬럼명 | 제약 조건 | 설명 |
|------|------|--------|-----------|------|
| `id` | `Long` | `id` | PK, NOT NULL, 수정 불가 | 기본키. `IDENTITY` 전략으로 1씩 자동 증가 |
| `title` | `String` | `title` | NOT NULL | 게시글 제목 (VARCHAR) |
| `content` | `String` | `content` | NOT NULL | 게시글 내용 (VARCHAR) |

### 어노테이션

- `@Entity` : JPA 엔티티로 등록
- `@Id` : 기본키 지정
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` : 기본키 자동 증가
- `@Column(name = ..., nullable = false)` : 컬럼 이름 및 NOT NULL 제약 지정 (`id`는 `updatable = false` 추가)
- `@Getter` (Lombok) : getter 자동 생성
- `@Builder` (Lombok) : 생성자에 빌더 패턴 적용

### 생성자

- `Articles(String title, String content)` : `@Builder`가 적용된 public 생성자. 빌더로 객체를 생성합니다.
- `protected Articles()` : JPA가 요구하는 기본 생성자. 외부에서 직접 생성하지 못하도록 `protected`로 제한합니다.

### Getter

`getId()`, `getTitle()`, `getContent()`

### 사용 예시

```java
Articles article = Articles.builder()
        .title("제목")
        .content("내용")
        .build();
```
------------------------------------------------------------------------------------------------------------------------------