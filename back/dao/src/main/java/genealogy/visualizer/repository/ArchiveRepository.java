package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Archive;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    @Query("select arch from Archive arch where arch.name = :archiveName")
    Optional<Archive> findArchivedByName(@Param("archiveName") String name);

    @Modifying
    @Query(value = "update archive a set " +
            "name = :#{#entity.name}, abbreviation = :#{#entity.abbreviation}, comment = :#{#entity.comment}, " +
            "address = :#{#entity.address} " +
            "where id = :#{#entity.id}", nativeQuery = true)
    Optional<Integer> update(@Param("entity") Archive entity);

    @Query("select a from Archive a where a.id = :id")
    @EntityGraph(value = "Archive.full", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Archive> findFullInfoById(@Param("id") Long id);

}
