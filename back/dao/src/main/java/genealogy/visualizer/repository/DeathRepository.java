package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Death;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeathRepository extends JpaRepository<Death, Long> {
}
