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
}
