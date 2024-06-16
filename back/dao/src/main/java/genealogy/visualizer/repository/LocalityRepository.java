package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Locality;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocalityRepository extends JpaRepository<Locality, Long> {

    @Query(value = "update locality c set " +
            "address = :#{#entity.address}, name = :#{#entity.name}, type = :#{#entity.type?.name} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    Locality update(@Param("entity") Locality entity);

    @Query("select l from Locality l where l.id = :id")
    @EntityGraph(value = "Locality.withAnotherNames", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Locality> findWithAnotherNames(@Param("id") Long id);

    @Query("select l from Locality l where l.id = :id")
    @EntityGraph(value = "Locality.withDeaths", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Locality> findWithDeaths(@Param("id") Long id);

    @Query("select l from Locality l where l.id = :id")
    @EntityGraph(value = "Locality.withChristenings", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Locality> findWithChristenings(@Param("id") Long id);

    @Query("select l from Locality l where l.id = :id")
    @EntityGraph(value = "Locality.withPersonsWithBirthLocality", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Locality> findWithPersonsWithBirthLocality(@Param("id") Long id);

    @Query("select l from Locality l where l.id = :id")
    @EntityGraph(value = "Locality.withPersonsWithDeathLocality", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Locality> findWithPersonsWithDeathLocality(@Param("id") Long id);

    @Query("select l from Locality l where l.id = :id")
    @EntityGraph(value = "Locality.withMarriagesWithWifeLocality", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Locality> findWithMarriagesWithWifeLocality(@Param("id") Long id);

    @Query("select l from Locality l where l.id = :id")
    @EntityGraph(value = "Locality.withMarriagesWithHusbandLocality", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Locality> findWithMarriagesWithHusbandLocality(@Param("id") Long id);

    @Modifying
    @Query(value = "delete from another_locality_name where locality_id = :id", nativeQuery = true)
    void deleteAnotherNamesById(@Param("id") Long id);

    @Modifying
    @Query(value = "insert into another_locality_name (locality_id, another_name) values (:id, :anotherName)", nativeQuery = true)
    void insertAnotherName(@Param("id") Long id, @Param("anotherName") String anotherName);

}
