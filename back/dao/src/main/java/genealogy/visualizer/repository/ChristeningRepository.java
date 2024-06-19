package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Christening;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @Modifying
    @Query(value = "update christening c set " +
            "birth_date = :#{#entity.birthDate}, christening_date = :#{#entity.christeningDate}, comment = :#{#entity.comment}, " +
            "father_last_name = :#{#entity.father?.lastName}, father_name = :#{#entity.father?.name}, father_status = :#{#entity.father?.status}, " +
            "father_surname = :#{#entity.father?.surname}, legitimacy = :#{#entity.legitimacy}, mother_last_name = :#{#entity.mother?.lastName}, " +
            "mother_name = :#{#entity.mother?.name}, mother_status = :#{#entity.mother?.status}, mother_surname = :#{#entity.mother?.surname}, " +
            "name = :#{#entity.name}, sex = :#{#entity.sex?.name}, archive_document_id = :#{#entity.archiveDocument?.id}, " +
            "locality_id = :#{#entity.locality?.id}, person_id = :#{#entity.person?.id} " +
            "where id = :#{#entity.id}", nativeQuery = true)
    Optional<Integer> update(@Param("entity") Christening entity);

    @Query("select c from Christening c where c.id = :id")
    @EntityGraph(value = "Christening.withGodParents", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Christening> findWithGodParents(@Param("id") Long id);

    @Query("select c from Christening c where c.id = :id")
    @EntityGraph(value = "Christening.withLocalityAndPersonAndArchiveDocument", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Christening> findWithLocalityAndPersonAndArchiveDocument(@Param("id") Long id);

    @Modifying
    @Query(value = "update christening set locality_id = :newLocalityId where locality_id = :localityId", nativeQuery = true)
    void updateLocalityId(@Param("localityId") Long localityId, @Param("newLocalityId") Long newLocalityId);

    @Modifying
    @Query(value = "update christening set locality_id = :newLocalityId where id = :id", nativeQuery = true)
    void updateLocalityIdById(@Param("id") Long id, @Param("newLocalityId") Long newLocalityId);

}
