package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Christening;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChristeningRepository extends JpaRepository<Christening, Long> {

    @Modifying
    @Query(value = "update christening set person_id = :newPersonId where person_id = :personId", nativeQuery = true)
    void updatePersonIdByPersonId(@Param("personId") Long personId, @Param("newPersonId") Long newPersonId);

    @Modifying
    @Query(value = "update christening set person_id = :newPersonId where id = :id", nativeQuery = true)
    void updatePersonIdById(@Param("id") Long id, @Param("newPersonId") Long newPersonId);

    @Modifying
    @Query(value = "update christening set archive_document_id = :newArchiveDocumentId where archive_document_id = :archiveDocumentId", nativeQuery = true)
    void updateArchiveDocumentId(@Param("archiveDocumentId") Long archiveDocumentId, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

    @Modifying
    @Query(value = "update christening set archive_document_id = :newArchiveDocumentId where id = :id", nativeQuery = true)
    void updateArchiveDocumentIdById(@Param("id") Long id, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

}
