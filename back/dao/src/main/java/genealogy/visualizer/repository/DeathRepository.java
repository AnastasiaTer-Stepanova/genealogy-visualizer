package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Death;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DeathRepository extends JpaRepository<Death, Long> {

    @Modifying
    @Query(value = "update death set person_id = :newPersonId where person_id = :personId", nativeQuery = true)
    void updatePersonIdByPersonId(@Param("personId") Long personId, @Param("newPersonId") Long newPersonId);

    @Modifying
    @Query(value = "update death set person_id = :newPersonId where id = :id", nativeQuery = true)
    void updatePersonIdById(@Param("id") Long id, @Param("newPersonId") Long newPersonId);
}
