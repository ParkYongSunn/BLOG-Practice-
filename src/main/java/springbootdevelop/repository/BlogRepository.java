package springbootdevelop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springbootdevelop.domain.Articles;

public interface BlogRepository extends JpaRepository<Articles, Long> {
}
