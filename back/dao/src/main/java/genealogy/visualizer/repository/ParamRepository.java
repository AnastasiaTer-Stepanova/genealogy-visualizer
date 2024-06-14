package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ParamRepository extends JpaRepository<Param, Long> {

    Optional<Param> findByName(String name);

    @Modifying
    @Query(value = "update param set value = :newValue where name = :name", nativeQuery = true)
    void updateValueByName(@org.springframework.data.repository.query.Param("name") String name,
                           @org.springframework.data.repository.query.Param("newValue") String newValue);

}
