package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Marriage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MarriageRepository extends JpaRepository<Marriage, Long> {

    @Modifying
    @Query(value = "update marriage set archive_document_id = :newArchiveDocumentId where archive_document_id = :archiveDocumentId", nativeQuery = true)
    void updateArchiveDocumentId(@Param("archiveDocumentId") Long archiveDocumentId, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

    @Modifying
    @Query(value = "update marriage set archive_document_id = :newArchiveDocumentId where id = :id", nativeQuery = true)
    void updateArchiveDocumentIdById(@Param("id") Long id, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

}
