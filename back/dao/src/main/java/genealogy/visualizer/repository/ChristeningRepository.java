package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Christening;
import genealogy.visualizer.entity.model.GodParent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

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

    @Query(value = "update christening c set " +
            "birth_date = :#{#entity.birthDate}, christening_date = :#{#entity.christeningDate}, comment = :#{#entity.comment}, " +
            "father_last_name = :#{#entity.father?.lastName}, father_name = :#{#entity.father?.name}, father_status = :#{#entity.father?.status}, " +
            "father_surname = :#{#entity.father?.surname}, legitimacy = :#{#entity.legitimacy}, mother_last_name = :#{#entity.mother?.lastName}, " +
            "mother_name = :#{#entity.mother?.name}, mother_status = :#{#entity.mother?.status}, mother_surname = :#{#entity.mother?.surname}, " +
            "name = :#{#entity.name}, sex = :#{#entity.sex?.name}, archive_document_id = :#{#entity.archiveDocument?.id}, " +
            "locality_id = :#{#entity.locality?.id}, person_id = :#{#entity.person?.id} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    Christening update(@Param("entity") Christening entity);

    @Query("select c from Christening c left join fetch c.archiveDocument left join fetch c.person left join fetch c.godParents " +
            "left join fetch c.locality where c.id = :id")
    Optional<Christening> findFullInfoById(@Param("id") Long id);

    @Modifying
    @Query(value = "update christening set locality_id = :newLocalityId where locality_id = :localityId", nativeQuery = true)
    void updateLocalityId(@Param("localityId") Long localityId, @Param("newLocalityId") Long newLocalityId);

    @Modifying
    @Query(value = "update god_parent set locality_id = :newLocalityId where locality_id = :localityId", nativeQuery = true)
    void updateGodParentLocalityId(@Param("localityId") Long localityId, @Param("newLocalityId") Long newLocalityId);

    @Modifying
    @Query(value = "update christening set locality_id = :newLocalityId where id = :id", nativeQuery = true)
    void updateLocalityIdById(@Param("id") Long id, @Param("newLocalityId") Long newLocalityId);

    @Modifying
    @Query(value = "delete from god_parent where christening_id = :id", nativeQuery = true)
    void deleteGodParentsById(@Param("id") Long id);

    @Modifying
    @Query(value = "insert into god_parent (christening_id, last_name, name, status, surname, locality_id, relative_last_name, " +
            "relative_name, relative_status, relative_surname) " +
            "values (:id, :#{#entity.fullName?.lastName}, :#{#entity.fullName?.name}, :#{#entity.fullName?.status}, " +
            ":#{#entity.fullName?.surname}, :#{#entity.locality?.id}, :#{#entity.relative?.lastName}, :#{#entity.relative?.name}, " +
            ":#{#entity.relative?.status}, :#{#entity.relative?.surname})",
            nativeQuery = true)
    void insertGodParent(@Param("id") Long id, @Param("entity") GodParent entity);
}
