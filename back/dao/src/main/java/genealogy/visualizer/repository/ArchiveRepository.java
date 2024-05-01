package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Archive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArchiveRepository extends JpaRepository<Archive, Long> {

    @Query("select arch from Archive arch where arch.name = :archiveName")
    Optional<Archive> findArchivedByName(@Param("archiveName") String name);
}
