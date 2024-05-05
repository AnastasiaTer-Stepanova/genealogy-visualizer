package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Marriage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarriageRepository extends JpaRepository<Marriage, Long> {
}
