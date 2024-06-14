package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParamRepository extends JpaRepository<Param, Long> {

    Optional<Param> findByName(String name);
}
