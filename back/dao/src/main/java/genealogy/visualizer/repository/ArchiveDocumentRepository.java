package genealogy.visualizer.repository;

import genealogy.visualizer.entity.ArchiveDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArchiveDocumentRepository extends JpaRepository<ArchiveDocument, Long> {

    @Query("select ad from ArchiveDocument ad join fetch ad.archive where ad.archive.name = :archiveName and " +
            "ad.fund = :fund  and ad.catalog = :catalog and ad.instance = :instance and ad.bunch = :bunch and ad.year = :year ")
    Optional<ArchiveDocument> findArchiveDocumentByConstraint(@Param("archiveName") String archiveName,
                                                              @Param("fund") String fund,
                                                              @Param("catalog") String catalog,
                                                              @Param("instance") String instance,
                                                              @Param("bunch") String bunch,
                                                              @Param("year") Short year);
}