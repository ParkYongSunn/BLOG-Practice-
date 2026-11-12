# BLOG-Practice-
BLOG (Practice)

## 프로젝트 구조

```
springbootdevelop
├── SpringSbApplication.java        # 애플리케이션 진입점
├── controller
│   └── BlogApiController.java      # REST API 컨트롤러
├── Service
│   └── BlogService.java            # 비즈니스 로직
├── repository
│   └── BlogRepository.java         # DB 접근 (Spring Data JPA)
├── domain
│   └── Articles.java               # JPA 엔티티
└── dto
    ├── AddArticleRequest.java      # 글 추가 요청 DTO
    ├── UpdateArticleRequest.java   # 글 수정 요청 DTO
    └── ArticleResponse.java        # 글 조회 응답 DTO
```

요청 흐름: `Controller → Service → Repository → DB(H2)`

## API 목록

| 기능 | 메서드 | URL | 요청 | 응답 |
|------|--------|-----|------|------|
| [블로그 글 추가](#1-블로그-글-추가-api) | `POST` | `/api/articles` | `AddArticleRequest` (JSON) | `201 Created` + `Articles` |
| [블로그 글 전체 조회](#2-블로그-글-전체-조회-api) | `GET` | `/api/articles` | 없음 | `200 OK` + `List<ArticleResponse>` |
| [블로그 글 단건 조회](#3-블로그-글-단건-조회-api) | `GET` | `/api/articles/{id}` | 경로 변수 `id` | `200 OK` + `ArticleResponse` |
| [블로그 글 삭제](#4-블로그-글-삭제-api) | `DELETE` | `/api/articles/{id}` | 경로 변수 `id` | `200 OK` (본문 없음) |
| [블로그 글 수정](#5-블로그-글-수정-api) | `PUT` | `/api/articles/{id}` | `UpdateArticleRequest` (JSON) | `200 OK` + 수정된 `Articles` |

------------------------------------------------------------------------------------------------------------------------------

# 공통 구성 요소

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
- `@NoArgsConstructor(access = AccessLevel.PROTECTED)` (Lombok) : `protected` 기본 생성자 자동 생성

### 생성자

- `Articles(String title, String content)` : `@Builder`가 적용된 public 생성자. 빌더로 객체를 생성합니다.
- `protected Articles()` : JPA가 요구하는 기본 생성자. 외부에서 직접 생성하지 못하도록 `protected`로 제한합니다.

### 메서드

- `update(String title, String content)` : 제목과 내용을 전달받은 값으로 변경합니다. (글 수정 API에서 사용)

### Getter

`getId()`, `getTitle()`, `getContent()`

### 사용 예시

```java
Articles article = Articles.builder()
        .title("제목")
        .content("내용")
        .build();
```

## BlogRepository

`springbootdevelop.repository.BlogRepository` — DB 접근을 담당하는 인터페이스입니다.

```java
public interface BlogRepository extends JpaRepository<Articles, Long> {
}
```

- `JpaRepository<Articles, Long>` 를 상속받으면 `Articles` 엔티티(PK 타입 `Long`)에 대한 `save()`, `findAll()`, `findById()`, `delete()` 등의 CRUD 메서드를 구현 없이 사용할 수 있습니다.
- 인터페이스만 선언하면 Spring Data JPA가 구현체를 자동으로 생성합니다.

## 클래스 공통 어노테이션

`BlogService`, `BlogApiController` 모두 아래 어노테이션을 사용합니다.

- `@RequiredArgsConstructor` (Lombok) : `final` 필드를 파라미터로 받는 생성자를 자동 생성 → 생성자 주입(DI)
- `@Service` : 스프링 빈(서비스 계층)으로 등록 (`BlogService`)
- `@RestController` : `@Controller` + `@ResponseBody`. 응답 본문(JSON)을 직접 반환하는 컨트롤러로 등록 (`BlogApiController`)

------------------------------------------------------------------------------------------------------------------------------

# 기능별 API

## 1. 블로그 글 추가 API

새 블로그 글을 저장합니다.

| 항목 | 내용 |
|------|------|
| 메서드 / URL | `POST /api/articles` |
| 요청 본문 | `AddArticleRequest` (JSON) |
| 성공 응답 | `201 Created` + 저장된 `Articles` |

### 처리 흐름

```
BlogApiController.addArticle()
  → BlogService.save()
    → AddArticleRequest.toEntity()      # DTO → 엔티티 변환
    → BlogRepository.save()             # DB 저장
```

### 요청 / 응답 예시

```http
POST /api/articles
Content-Type: application/json

{
  "title": "제목",
  "content": "내용"
}
```

```http
HTTP/1.1 201 Created

{
  "id": 4,
  "title": "제목",
  "content": "내용"
}
```

### 계층별 코드

**Controller** — `BlogApiController.addArticle()`

```java
@PostMapping("/api/articles")
public ResponseEntity<Articles> addArticle(@RequestBody AddArticleRequest request){
    Articles savedArticle = blogService.save(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);
}
```

- `@PostMapping("/api/articles")` : HTTP POST 요청을 해당 메서드에 매핑
- `@RequestBody` : 요청 본문(JSON)을 `AddArticleRequest` 객체로 변환
- `ResponseEntity.status(HttpStatus.CREATED)` : 응답 코드 `201 Created` 지정

**Service** — `BlogService.save()`

```java
public Articles save(AddArticleRequest request){
    return blogRepository.save(request.toEntity());
}
```

- 요청 DTO를 엔티티로 변환한 뒤 `save()`로 저장하고, 저장된 엔티티를 반환합니다.

**DTO** — `AddArticleRequest`

```java
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {
    private String title;
    private String content;

    public Articles toEntity(){
        return Articles.builder().title(title).content(content).build();
    }
}
```

- `@NoArgsConstructor` (Lombok) : 기본 생성자 자동 생성 (JSON 역직렬화에 필요)
- `@AllArgsConstructor` (Lombok) : 모든 필드 값을 파라미터로 받는 생성자 자동 생성
- `@Getter` (Lombok) : getter 자동 생성
- `toEntity()` : 빌더 패턴으로 DTO를 `Articles` 엔티티로 변환

| 필드 | 타입 | 설명 |
|------|------|------|
| `title` | `String` | 게시글 제목 |
| `content` | `String` | 게시글 내용 |

------------------------------------------------------------------------------------------------------------------------------

## 2. 블로그 글 전체 조회 API

저장된 모든 블로그 글을 조회합니다.

| 항목 | 내용 |
|------|------|
| 메서드 / URL | `GET /api/articles` |
| 요청 본문 | 없음 |
| 성공 응답 | `200 OK` + `List<ArticleResponse>` |

### 처리 흐름

```
BlogApiController.findAllArticles()
  → BlogService.findAll()
    → BlogRepository.findAll()          # 전체 조회
  → ArticleResponse::new                # 엔티티 → 응답 DTO 변환
```

### 요청 / 응답 예시

```http
GET /api/articles
```

```http
HTTP/1.1 200 OK

[
  { "title": "제목 1", "content": "내용 1" },
  { "title": "제목 2", "content": "내용 2" },
  { "title": "제목 3", "content": "내용 3" }
]
```

### 계층별 코드

**Controller** — `BlogApiController.findAllArticles()`

```java
@GetMapping("/api/articles")
public ResponseEntity<List<ArticleResponse>> findAllArticles(){
    List<ArticleResponse> articles = blogService.findAll().stream().map(ArticleResponse::new).toList();
    return ResponseEntity.ok().body(articles);
}
```

- `@GetMapping("/api/articles")` : HTTP GET 요청을 해당 메서드에 매핑
- `stream().map(ArticleResponse::new).toList()` : 엔티티 목록을 응답 DTO 목록으로 변환
- `ResponseEntity.ok()` : 응답 코드 `200 OK` 지정

**Service** — `BlogService.findAll()`

```java
public List<Articles> findAll(){
    return blogRepository.findAll();
}
```

- JPA 지원 메서드인 `findAll()`을 호출해 `articles` 테이블에 저장된 모든 데이터를 조회합니다.

**DTO** — `ArticleResponse`

```java
@Getter
public class ArticleResponse {
    private final String title;
    private final String content;

    public ArticleResponse(Articles articles){
        this.title = articles.getTitle();
        this.content = articles.getContent();
    }
}
```

- `@Getter` (Lombok) : getter 자동 생성
- 엔티티를 받아 `title`, `content` 값을 복사합니다 (`id`는 응답에 포함하지 않음)

| 필드 | 타입 | 설명 |
|------|------|------|
| `title` | `String` | 게시글 제목 |
| `content` | `String` | 게시글 내용 |

------------------------------------------------------------------------------------------------------------------------------

## 3. 블로그 글 단건 조회 API

`id`로 블로그 글 1건을 조회합니다.

| 항목 | 내용 |
|------|------|
| 메서드 / URL | `GET /api/articles/{id}` |
| 요청 본문 | 없음 (경로 변수 `id`) |
| 성공 응답 | `200 OK` + `ArticleResponse` |
| 실패 | 해당 `id`가 없으면 `IllegalArgumentException("not found : {id}")` 발생 |

### 처리 흐름

```
BlogApiController.findArticle()
  → BlogService.findById()
    → BlogRepository.findById()         # id로 조회, 없으면 예외
  → new ArticleResponse(articles)       # 엔티티 → 응답 DTO 변환
```

### 요청 / 응답 예시

```http
GET /api/articles/1
```

```http
HTTP/1.1 200 OK

{ "title": "제목 1", "content": "내용 1" }
```

### 계층별 코드

**Controller** — `BlogApiController.findArticle()`

```java
@GetMapping("/api/articles/{id}")
public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){
    Articles articles = blogService.findById(id);
    return ResponseEntity.ok().body(new ArticleResponse(articles));
}
```

- `@GetMapping("/api/articles/{id}")` : URL 경로의 `{id}` 부분을 변수로 받는 GET 매핑
- `@PathVariable` : URL 경로의 `{id}` 값을 메서드 파라미터로 바인딩

**Service** — `BlogService.findById()`

```java
public Articles findById(long id){
    return blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));
}
```

- `findById()`는 `Optional<Articles>`를 반환하며, 값이 없으면 `orElseThrow()`로 예외를 던집니다.

------------------------------------------------------------------------------------------------------------------------------

## 4. 블로그 글 삭제 API

`id`로 블로그 글 1건을 삭제합니다.

| 항목 | 내용 |
|------|------|
| 메서드 / URL | `DELETE /api/articles/{id}` |
| 요청 본문 | 없음 (경로 변수 `id`) |
| 성공 응답 | `200 OK` (본문 없음) |

### 처리 흐름

```
BlogApiController.deleteArticles()
  → BlogService.delete()
    → BlogRepository.deleteById()       # id로 삭제
```

### 요청 / 응답 예시

```http
DELETE /api/articles/1
```

```http
HTTP/1.1 200 OK
```

### 계층별 코드

**Controller** — `BlogApiController.deleteArticles()`

```java
@DeleteMapping("/api/articles/{id}")
public ResponseEntity<Void> deleteArticles(@PathVariable long id){
    blogService.delete(id);
    return ResponseEntity.ok().build();
}
```

- `@DeleteMapping("/api/articles/{id}")` : HTTP DELETE 요청을 해당 메서드에 매핑
- `ResponseEntity<Void>` / `.build()` : 응답 본문 없이 상태 코드만 반환

**Service** — `BlogService.delete()`

```java
public void delete(Long id){
    blogRepository.deleteById(id);
}
```

------------------------------------------------------------------------------------------------------------------------------

## 5. 블로그 글 수정 API

`id`에 해당하는 블로그 글의 제목과 내용을 수정합니다.

| 항목 | 내용 |
|------|------|
| 메서드 / URL | `PUT /api/articles/{id}` |
| 요청 본문 | `UpdateArticleRequest` (JSON) |
| 성공 응답 | `200 OK` + 수정된 `Articles` |
| 실패 | 해당 `id`가 없으면 `IllegalArgumentException("not found : {id}")` 발생 |

### 처리 흐름

```
BlogApiController.updateArticles()
  → BlogService.update()
    → BlogRepository.findById()         # 수정할 글 조회, 없으면 예외
    → Articles.update()                 # 제목/내용 변경
    → (트랜잭션 종료 시 변경 감지로 UPDATE 자동 실행)
```

### 요청 / 응답 예시

```http
PUT /api/articles/1
Content-Type: application/json

{
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

```http
HTTP/1.1 200 OK

{
  "id": 1,
  "title": "수정된 제목",
  "content": "수정된 내용"
}
```

### 계층별 코드

**Controller** — `BlogApiController.updateArticles()`

```java
@PutMapping("/api/articles/{id}")
public ResponseEntity<Articles> updateArticles(@PathVariable long id, @RequestBody UpdateArticleRequest request){
    Articles updatedArticles = blogService.update(id, request);
    return ResponseEntity.ok().body(updatedArticles);
}
```

- `@PutMapping("/api/articles/{id}")` : HTTP PUT 요청을 해당 메서드에 매핑
- `@PathVariable` / `@RequestBody` : 경로의 `id`와 요청 본문(JSON)을 각각 바인딩
- `ResponseEntity.ok()` : 수정된 엔티티를 담아 `200 OK` 응답

**Service** — `BlogService.update()`

```java
@Transactional
public Articles update(long id, UpdateArticleRequest request){
    Articles articles = blogRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    articles.update(request.getTitle(), request.getContent());
    return articles;
}
```

- `@Transactional` (`jakarta.transaction`) : 메서드를 하나의 트랜잭션으로 묶습니다. 트랜잭션 안에서 엔티티 값이 바뀌면 JPA **변경 감지(dirty checking)** 로 종료 시점에 UPDATE 쿼리가 자동 실행되므로 `save()`를 따로 호출하지 않아도 됩니다.

**Entity** — `Articles.update()`

```java
public void update(String title, String content){
    this.title = title;
    this.content = content;
}
```

**DTO** — `UpdateArticleRequest`

```java
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateArticleRequest {
    private String title;
    private String content;
}
```

- `@NoArgsConstructor` / `@AllArgsConstructor` / `@Getter` (Lombok) : 기본 생성자, 전체 필드 생성자, getter 자동 생성

| 필드 | 타입 | 설명 |
|------|------|------|
| `title` | `String` | 수정할 제목 |
| `content` | `String` | 수정할 내용 |

------------------------------------------------------------------------------------------------------------------------------

# 설정 파일

## application.yaml

| 설정 | 값 | 설명 |
|------|----|------|
| `spring.application.name` | `Spring_Sb` | 애플리케이션 이름 |
| `spring.jpa.show-sql` | `true` | 실행되는 SQL을 콘솔에 출력 |
| `spring.jpa.defer-datasource-initialization` | `true` | Hibernate 테이블 생성 이후에 SQL 스크립트(`import.sql` 등) 실행 |
| `spring.jpa.properties.hibernate.format_sql` | `true` | 출력되는 SQL을 보기 좋게 포맷팅 |
| `spring.sql.init.mode` | `never` | `data.sql`/`schema.sql` 자동 실행 안 함 (Hibernate의 `import.sql`은 별도로 실행됨) |
| `spring.datasource.url` | `jdbc:h2:mem:testdb` | H2 인메모리 DB 사용 |
| `spring.datasource.username` | `sa` | DB 사용자 이름 |
| `spring.h2.console.enabled` | `true` | H2 웹 콘솔 활성화 (`/h2-console`) |

## import.sql

애플리케이션 시작 시 `articles` 테이블에 테스트용 초기 데이터를 넣습니다.

```sql
INSERT INTO articles (title, content) VALUES ('제목 1', '내용 1')
INSERT INTO articles (title, content) VALUES ('제목 2', '내용 2')
INSERT INTO articles (title, content) VALUES ('제목 3', '내용 3')
```

------------------------------------------------------------------------------------------------------------------------------

# 주요 어노테이션 정리

| 어노테이션 | 분류 | 설명 |
|-----------|------|------|
| `@Entity` | JPA | 클래스를 DB 테이블과 매핑되는 엔티티로 지정 |
| `@Id` | JPA | 기본키 필드 지정 |
| `@GeneratedValue` | JPA | 기본키 생성 전략 지정 (`IDENTITY` : DB가 자동 증가) |
| `@Column` | JPA | 컬럼 이름, NOT NULL, 수정 가능 여부 등 지정 |
| `@Service` | Spring | 서비스 계층 빈으로 등록 |
| `@RestController` | Spring | `@Controller` + `@ResponseBody`. 반환값을 JSON으로 응답 |
| `@PostMapping` / `@GetMapping` / `@PutMapping` / `@DeleteMapping` | Spring | HTTP POST / GET / PUT / DELETE 요청을 메서드에 매핑 |
| `@PathVariable` | Spring | URL 경로의 `{id}` 값을 메서드 파라미터로 바인딩 |
| `@RequestBody` | Spring | 요청 본문(JSON)을 자바 객체로 변환 |
| `@Transactional` | 트랜잭션 | 메서드를 하나의 트랜잭션으로 처리. 엔티티 변경 시 변경 감지로 자동 UPDATE |
| `@Getter` | Lombok | getter 메서드 자동 생성 |
| `@Builder` | Lombok | 빌더 패턴 코드 자동 생성 |
| `@NoArgsConstructor` | Lombok | 기본 생성자 자동 생성 |
| `@AllArgsConstructor` | Lombok | 모든 필드를 받는 생성자 자동 생성 |
| `@RequiredArgsConstructor` | Lombok | `final` 필드를 받는 생성자 자동 생성 (생성자 주입) |
------------------------------------------------------------------------------------------------------------------------------
