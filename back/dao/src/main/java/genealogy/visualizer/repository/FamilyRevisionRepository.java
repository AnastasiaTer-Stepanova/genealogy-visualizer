package genealogy.visualizer.repository;

import genealogy.visualizer.entity.FamilyRevision;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRevisionRepository extends JpaRepository<FamilyRevision, Long> {
}
