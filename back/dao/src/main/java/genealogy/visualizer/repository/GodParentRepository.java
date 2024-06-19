package genealogy.visualizer.repository;

import genealogy.visualizer.entity.GodParent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GodParentRepository extends JpaRepository<GodParent, Long> {

    @Modifying
    @Query(value = "update god_parent set locality_id = :newLocalityId where locality_id = :localityId", nativeQuery = true)
    void updateGodParentLocalityId(@Param("localityId") Long localityId, @Param("newLocalityId") Long newLocalityId);

    @Modifying
    @Query(value = "delete from god_parent where christening_id = :christeningId", nativeQuery = true)
    void deleteGodParentsByChristeningId(@Param("christeningId") Long christeningId);

}
