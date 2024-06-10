package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    @Query("select arch from Archive arch where arch.name = :archiveName")
    Optional<Archive> findArchivedByName(@Param("archiveName") String name);

    @Query(value = "update archive a set " +
            "name = :#{#entity.name}, abbreviation = :#{#entity.abbreviation}, comment = :#{#entity.comment}, " +
            "address = :#{#entity.address} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    Archive update(@Param("entity") Archive entity);

    @Query("select a from Archive a left join fetch a.archiveDocuments where a.id = :id ")
    Optional<Archive> findByIdWithArchiveDocuments(@Param("id") Long id);

}
