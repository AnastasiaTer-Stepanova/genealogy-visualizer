package genealogy.visualizer.repository;

import genealogy.visualizer.entity.FamilyRevision;
import org.springframework.data.jpa.repository.JpaRepository;
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
    List<String> getAnotherNames(@Param("id") Long id);

    @Query(value = "select fs from FamilyRevision fs left join fetch fs.anotherNames an left join fetch fs.archiveDocument ad left join fetch ad.archive " +
            "left join fetch ad.nextRevision left join fetch fs.partner left join fetch fs.person where fs.id = :id")
    Optional<FamilyRevision> findFullInfoById(@Param("id") Long id);

    @Query("select fs from FamilyRevision fs left join fetch fs.archiveDocument ad left join fetch ad.archive " +
            "where ad.id = :archiveDocumentId and fs.familyRevisionNumber = :number")
    Optional<List<FamilyRevision>> findFamilyRevisionsByNumberFamilyAndArchiveDocumentId(@Param("archiveDocumentId") Long archiveDocumentId,
                                                                                         @Param("number") Short number);

    @Query("select fs from FamilyRevision fs left join fetch fs.archiveDocument ad left join fetch ad.archive " +
            "where ad.id = :archiveDocumentId and fs.nextFamilyRevisionNumber = :nextFamilyRevisionNumber")
    Optional<List<FamilyRevision>> findFamilyRevisionsByNextFamilyRevisionNumberAndArchiveDocumentId(@Param("archiveDocumentId") Long archiveDocumentId,
                                                                                                     @Param("nextFamilyRevisionNumber") Short nextFamilyRevisionNumber);
}
