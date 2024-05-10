package genealogy.visualizer.repository;

import genealogy.visualizer.entity.FamilyRevision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FamilyRevisionRepository extends JpaRepository<FamilyRevision, Long> {

    @Query(value = "update family_revision fs set " +
            "age = :#{#entity.age?.age}, age_type = :#{#entity.age?.type?.name}, age_in_next_revision = :#{#entity.ageInNextRevision?.age}, " +
            "age_type_in_next_revision = :#{#entity.ageInNextRevision?.type?.name}, age_in_previous_revision = :#{#entity.ageInPreviousRevision?.age}, " +
            "age_type_in_previous_revision = :#{#entity.ageInPreviousRevision?.type?.name}, arrived = :#{#entity.arrived}, comment = :#{#entity.comment}, " +
            "departed = :#{#entity.departed}, family_generation = :#{#entity.familyGeneration}, family_revision_number = :#{#entity.familyRevisionNumber}, " +
            "last_name = :#{#entity.fullName?.lastName}, name = :#{#entity.fullName?.name}, status = :#{#entity.fullName?.status}, " +
            "surname = :#{#entity.fullName?.surname}, is_head_of_yard = :#{#entity.headOfYard}, list_number = :#{#entity.listNumber}, " +
            "next_family_revision_number = :#{#entity.nextFamilyRevisionNumber}, previous_family_revision_number = :#{#entity.previousFamilyRevisionNumber}, " +
            "relative_last_name = :#{#entity.relative?.lastName}, relative_name = :#{#entity.relative?.name}, relative_status = :#{#entity.relative?.status}, " +
            "relative_surname = :#{#entity.relative?.surname}, sex = :#{#entity.sex?.name}, archive_document_id = :#{#entity.archiveDocument?.id}, " +
            "partner_id = :#{#entity.partner?.id}, person_id = :#{#entity.person?.id} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    FamilyRevision update(@Param("entity") FamilyRevision entity);

    @Query(value = "select anir.another_name from another_name_in_revision anir where anir.family_revision_id = :id",
            nativeQuery = true)
    List<String> getAnotherNames(@Param("id") Long id);
}
