package genealogy.visualizer.repository;

import genealogy.visualizer.entity.FamilyRevision;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FamilyRevisionRepository extends JpaRepository<FamilyRevision, Long> {

    @Query(value = "update family_revision fs set " +
            "age = :#{#entity.age?.age}, age_type = :#{#entity.age?.type?.name}, age_in_next_revision = :#{#entity.ageInNextRevision?.age}, " +
            "age_type_in_next_revision = :#{#entity.ageInNextRevision?.type?.name}, arrived = :#{#entity.arrived}, comment = :#{#entity.comment}, " +
            "departed = :#{#entity.departed}, family_generation = :#{#entity.familyGeneration}, family_revision_number = :#{#entity.familyRevisionNumber}, " +
            "last_name = :#{#entity.fullName?.lastName}, name = :#{#entity.fullName?.name}, status = :#{#entity.fullName?.status}, " +
            "surname = :#{#entity.fullName?.surname}, is_head_of_yard = :#{#entity.headOfYard}, list_number = :#{#entity.listNumber}, " +
            "next_family_revision_number = :#{#entity.nextFamilyRevisionNumber}, " +
            "relative_last_name = :#{#entity.relative?.lastName}, relative_name = :#{#entity.relative?.name}, relative_status = :#{#entity.relative?.status}, " +
            "relative_surname = :#{#entity.relative?.surname}, sex = :#{#entity.sex?.name}, archive_document_id = :#{#entity.archiveDocument?.id}, " +
            "partner_id = :#{#entity.partner?.id}, person_id = :#{#entity.person?.id} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    FamilyRevision update(@Param("entity") FamilyRevision entity);

    @Query(value = "select anir.another_name from another_name_in_revision anir where anir.family_revision_id = :id",
            nativeQuery = true)
    List<String> findAnotherNames(@Param("id") Long id);

    @Query("select fr from FamilyRevision fr where fr.id = :id")
    @EntityGraph(value = "FamilyRevision.full", type = EntityGraph.EntityGraphType.LOAD)
    Optional<FamilyRevision> findFullInfoById(@Param("id") Long id);

    @Modifying
    @Query(value = "update family_revision set person_id = :newPersonId where person_id = :personId", nativeQuery = true)
    void updatePersonIdByPersonId(@Param("personId") Long personId, @Param("newPersonId") Long newPersonId);

    @Modifying
    @Query(value = "update family_revision set person_id = :newPersonId where id = :id", nativeQuery = true)
    void updatePersonIdById(@Param("id") Long id, @Param("newPersonId") Long newPersonId);

    @Modifying
    @Query(value = "update family_revision set archive_document_id = :newArchiveDocumentId where archive_document_id = :archiveDocumentId", nativeQuery = true)
    void updateArchiveDocumentId(@Param("archiveDocumentId") Long archiveDocumentId, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

    @Modifying
    @Query(value = "update family_revision set archive_document_id = :newArchiveDocumentId where id = :id", nativeQuery = true)
    void updateArchiveDocumentIdById(@Param("id") Long id, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

    @Modifying
    @Query(value = "update family_revision set partner_id = :newPartnerId where id = :id", nativeQuery = true)
    void updatePartnerIdById(@Param("id") Long id, @Param("newPartnerId") Long newPartnerId);

    @Modifying
    @Query(value = "update family_revision set partner_id = :newPartnerId where partner_id = :partnerId", nativeQuery = true)
    void updatePartnerId(@Param("partnerId") Long partnerId, @Param("newPartnerId") Long newPartnerId);

    @Modifying
    @Query(value = "delete from another_name_in_revision where family_revision_id = :id", nativeQuery = true)
    void deleteAnotherNamesById(@Param("id") Long id);

    @Modifying
    @Query(value = "insert into another_name_in_revision (family_revision_id, another_name) values (:id, :anotherName)", nativeQuery = true)
    void insertAnotherName(@Param("id") Long id, @Param("anotherName") String anotherName);
}
