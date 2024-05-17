package genealogy.visualizer.repository;

import genealogy.visualizer.entity.ArchiveDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ArchiveDocumentRepository extends JpaRepository<ArchiveDocument, Long> {

    @Query("select ad from ArchiveDocument ad join fetch ad.archive where ad.archive.name = :#{#entity.archive.name} and ad.fund = :#{#entity.fund} " +
            "and ad.catalog =  :#{#entity.catalog} and ad.instance = :#{#entity.instance} and ad.bunch = :#{#entity.bunch} and " +
            "ad.year = :#{#entity.year} and ad.type = :#{#entity.type}")
    Optional<ArchiveDocument> findArchiveDocumentByConstraint(@Param("entity") ArchiveDocument entity);

    @Query("select ad from ArchiveDocument ad join fetch ad.archive join fetch ad.familyRevisions fs " +
            "where ad.id = :archiveDocumentId and fs.familyRevisionNumber = :number")
    Optional<ArchiveDocument> findArchiveDocumentWithFamilyRevisionByNumberFamily(@Param("archiveDocumentId") Long archiveDocumentId,
                                                                                  @Param("number") short number);

    @Modifying
    @Query(value = "update archive_document ad set next_revision_id = :nextRevisionId where id = :id", nativeQuery = true)
    void updateNextRevisionId(@Param("id") Long id, @Param("nextRevisionId") Long nextRevisionId);

    @NonNull
    @Override
    @Query(value = "select ad from ArchiveDocument ad left join fetch ad.archive a where ad.id = :id")
    Optional<ArchiveDocument> findById(@NonNull @Param("id") Long id);

}
