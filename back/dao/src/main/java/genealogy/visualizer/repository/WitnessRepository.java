package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Witness;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WitnessRepository extends JpaRepository<Witness, Long> {

    @Modifying
    @Query(value = "delete from witness where marriage_id = :marriageId", nativeQuery = true)
    void deleteWitnessesByMarriageId(@Param("marriageId") Long marriageId);

    @Modifying
    @Query(value = "update witness set locality_id = :newLocalityId where locality_id = :localityId", nativeQuery = true)
    void updateWitnessLocalityId(@Param("localityId") Long localityId, @Param("newLocalityId") Long newLocalityId);
}
