package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Death;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeathRepository extends JpaRepository<Death, Long> {

    @Modifying
    @Query(value = "update death set person_id = :newPersonId where person_id = :personId", nativeQuery = true)
    void updatePersonIdByPersonId(@Param("personId") Long personId, @Param("newPersonId") Long newPersonId);

    @Modifying
    @Query(value = "update death set person_id = :newPersonId where id = :id", nativeQuery = true)
    void updatePersonIdById(@Param("id") Long id, @Param("newPersonId") Long newPersonId);

    @Modifying
    @Query(value = "update death set archive_document_id = :newArchiveDocumentId where archive_document_id = :archiveDocumentId", nativeQuery = true)
    void updateArchiveDocumentId(@Param("archiveDocumentId") Long archiveDocumentId, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

    @Modifying
    @Query(value = "update death set archive_document_id = :newArchiveDocumentId where id = :id", nativeQuery = true)
    void updateArchiveDocumentIdById(@Param("id") Long id, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

    @Query(value = "update death set " +
            "age = :#{#entity.age?.age}, age_type = :#{#entity.age?.type?.name}, burial_place = :#{#entity.burialPlace}, " +
            "last_name = :#{#entity.fullName?.lastName}, name = :#{#entity.fullName?.name}, status = :#{#entity.fullName?.status}, " +
            "surname = :#{#entity.fullName?.surname}, relative_last_name = :#{#entity.relative?.lastName}, relative_name = :#{#entity.relative?.name}, " +
            "relative_status = :#{#entity.relative?.status}, relative_surname = :#{#entity.relative?.surname}, " +
            "cause = :#{#entity.cause}, comment = :#{#entity.comment}, date = :#{#entity.date}, person_id = :#{#entity.person?.id}, " +
            "archive_document_id = :#{#entity.archiveDocument?.id}, locality_id = :#{#entity.locality?.id} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    Death update(@Param("entity") Death entity);

    @Query("select d from Death d where d.id = :id")
    @EntityGraph(value = "Death.full", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Death> findFullInfoById(@Param("id") Long id);

    @Modifying
    @Query(value = "update death set locality_id = :newLocalityId where locality_id = :localityId", nativeQuery = true)
    void updateLocalityId(@Param("localityId") Long localityId, @Param("newLocalityId") Long newLocalityId);

    @Modifying
    @Query(value = "update death set locality_id = :newLocalityId where id = :id", nativeQuery = true)
    void updateLocalityIdById(@Param("id") Long id, @Param("newLocalityId") Long newLocalityId);

}
