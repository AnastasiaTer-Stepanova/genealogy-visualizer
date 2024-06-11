package genealogy.visualizer.repository;

import genealogy.visualizer.entity.ArchiveDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface ArchiveDocumentRepository extends JpaRepository<ArchiveDocument, Long> {

    @Query("select ad from ArchiveDocument ad left join fetch ad.familyRevisions fs " +
            "where ad.id = :archiveDocumentId and fs.familyRevisionNumber = :number")
    Optional<ArchiveDocument> findArchiveDocumentWithFamilyRevisionByNumberFamily(@Param("archiveDocumentId") Long archiveDocumentId,
                                                                                  @Param("number") short number);

    @Modifying
    @Query(value = "update archive_document ad set next_revision_id = :nextRevisionId where id = :id", nativeQuery = true)
    void updateNextRevisionIdById(@Param("id") Long id, @Param("nextRevisionId") Long nextRevisionId);

    @Modifying
    @Query(value = "update archive_document ad set next_revision_id = :newNextRevisionId where next_revision_id = :nextRevisionId", nativeQuery = true)
    void updateNextRevisionId(@Param("nextRevisionId") Long nextRevisionId, @Param("newNextRevisionId") Long newNextRevisionId);

    @NonNull
    @Override
    @Query(value = "select ad from ArchiveDocument ad left join fetch ad.archive a where ad.id = :id")
    Optional<ArchiveDocument> findById(@NonNull @Param("id") Long id);

    @Modifying
    @Query(value = "update archive_document set archive_id = :newArchiveId where archive_id = :archiveId", nativeQuery = true)
    void updateArchiveId(@Param("archiveId") Long archiveId, @Param("newArchiveId") Long newArchiveId);

    @Modifying
    @Query(value = "update archive_document set archive_id = :newArchiveId where id = :archiveDocumentId", nativeQuery = true)
    void updateArchiveIdById(@Param("archiveDocumentId") Long archiveDocumentId, @Param("newArchiveId") Long newArchiveId);

    @Query(value = "update archive_document a set " +
            "type = :#{#entity.type?.name}, name = :#{#entity.name}, abbreviation = :#{#entity.abbreviation}, " +
            "year = :#{#entity.year}, fund = :#{#entity.fund}, catalog = :#{#entity.catalog}, instance = :#{#entity.instance}, " +
            "bunch = :#{#entity.bunch}, next_revision_id = :#{#entity.nextRevision?.id}, archive_id = :#{#entity.archive?.id} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    ArchiveDocument update(@Param("entity") ArchiveDocument entity);

    @Query("select ad from ArchiveDocument ad left join fetch ad.archive left join fetch ad.previousRevisions " +
            "left join fetch ad.nextRevision where ad.id = :id ")
    Optional<ArchiveDocument> findFullInfoById(@Param("id") Long id);

    @Query("select ad from ArchiveDocument ad left join fetch ad.deaths where ad.id = :id ")
    Optional<ArchiveDocument> findByIdWithDeath(@Param("id") Long id);

    @Query("select ad from ArchiveDocument ad left join fetch ad.marriages where ad.id = :id ")
    Optional<ArchiveDocument> findByIdWithMarriages(@Param("id") Long id);

    @Query("select ad from ArchiveDocument ad left join fetch ad.familyRevisions where ad.id = :id ")
    Optional<ArchiveDocument> findByIdWithFamilyRevisions(@Param("id") Long id);

    @Query("select ad from ArchiveDocument ad left join fetch ad.christenings where ad.id = :id ")
    Optional<ArchiveDocument> findByIdWithChristenings(@Param("id") Long id);
}
