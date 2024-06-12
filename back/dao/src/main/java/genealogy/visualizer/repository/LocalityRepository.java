package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Locality;
import genealogy.visualizer.entity.enums.LocalityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocalityRepository extends JpaRepository<Locality, Long> {

    @Query(value = "select locality from Locality locality where locality.name = :name " +
            "and (locality.type is null or locality.type = :type) " +
            "and (locality.address is null or locality.address = :address) " +
            "order by locality.address asc, locality.type asc, locality.name asc limit 1")
    Optional<Locality> findLocality(@Param("name") String name,
                                    @Param("type") LocalityType type,
                                    @Param("address") String address);

    @Query(value = "update locality c set " +
            "address = :#{#entity.address}, name = :#{#entity.name}, type = :#{#entity.type?.name} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    Locality update(@Param("entity") Locality entity);

    @Query("select l from Locality l left join fetch l.anotherNames " +
            "where l.id = :id")
    Optional<Locality> findFullInfoById(@Param("id") Long id);

    @Query("select l from Locality l " +
            "left join fetch l.marriagesWithWifeLocality " +
            "where l.id = :id")
    Optional<Locality> findFullInfoByIdWithMarriageWifeLocality(@Param("id") Long id);

    @Query("select l from Locality l " +
            "left join fetch l.marriagesWithHusbandLocality " +
            "where l.id = :id")
    Optional<Locality> findFullInfoByIdWithMarriageHusbandLocality(@Param("id") Long id);

    @Query("select l from Locality l " +
            "left join fetch l.personsWithDeathLocality " +
            "where l.id = :id")
    Optional<Locality> findFullInfoByIdWithPersonDeathLocality(@Param("id") Long id);

    @Query("select l from Locality l " +
            "left join fetch l.personsWithBirthLocality " +
            "where l.id = :id")
    Optional<Locality> findFullInfoByIdWithPersonBirthLocality(@Param("id") Long id);

    @Query("select l from Locality l left join fetch l.christenings where l.id = :id")
    Optional<Locality> findFullInfoByIdWithChristening(@Param("id") Long id);

    @Query("select l from Locality l left join fetch l.deaths where l.id = :id")
    Optional<Locality> findFullInfoByIdWithDeath(@Param("id") Long id);

}
