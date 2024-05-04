package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Christening;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChristeningRepository extends JpaRepository<Christening, Integer> {

}
