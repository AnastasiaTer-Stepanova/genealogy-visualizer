package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Marriage;
import genealogy.visualizer.entity.model.Witness;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MarriageRepository extends JpaRepository<Marriage, Long> {

    @Modifying
    @Query(value = "update marriage set archive_document_id = :newArchiveDocumentId where archive_document_id = :archiveDocumentId", nativeQuery = true)
    void updateArchiveDocumentId(@Param("archiveDocumentId") Long archiveDocumentId, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

    @Modifying
    @Query(value = "update marriage set archive_document_id = :newArchiveDocumentId where id = :id", nativeQuery = true)
    void updateArchiveDocumentIdById(@Param("id") Long id, @Param("newArchiveDocumentId") Long newArchiveDocumentId);

    @Query(value = "update marriage c set " +
            "comment = :#{#entity.comment}, date = :#{#entity.date}, husband_last_name = :#{#entity.husband?.lastName}, " +
            "husband_name = :#{#entity.husband?.name}, husband_status = :#{#entity.husband?.status}, husband_surname = :#{#entity.husband?.surname}, " +
            "husband_age = :#{#entity.husbandAge?.age}, husband_age_type = :#{#entity.husbandAge?.type?.name}, " +
            "husband_marriage_number = :#{#entity.husbandMarriageNumber}, husbands_father_last_name = :#{#entity.husbandsFather?.lastName}, " +
            "husbands_father_name = :#{#entity.husbandsFather?.name}, husbands_father_status = :#{#entity.husbandsFather?.status}, " +
            "husbands_father_surname = :#{#entity.husbandsFather?.surname}, wife_last_name = :#{#entity.wife?.lastName}, " +
            "wife_name = :#{#entity.wife?.name}, wife_status = :#{#entity.wife?.status}, wife_surname = :#{#entity.wife?.surname}, " +
            "wife_age = :#{#entity.wifeAge?.age}, wife_age_type = :#{#entity.wifeAge?.type?.name}, wife_marriage_number = :#{#entity.wifeMarriageNumber}, " +
            "wifes_father_last_name = :#{#entity.wifesFather?.lastName}, wifes_father_name = :#{#entity.wifesFather?.name}, " +
            "wifes_father_status = :#{#entity.wifesFather?.status}, wifes_father_surname = :#{#entity.wifesFather?.surname}, " +
            "archive_document_id = :#{#entity.archiveDocument?.id}, husband_locality_id = :#{#entity.husbandLocality?.id}, " +
            "wife_locality_id = :#{#entity.wifeLocality?.id} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    Marriage update(@Param("entity") Marriage entity);

    @Modifying
    @Query(value = "delete from person_marriage where person_id = :personId and marriage_id = :marriageId", nativeQuery = true)
    void deletePersonMarriageLinkByPersonIdAndMarriageId(@Param("personId") Long personId, @Param("marriageId") Long marriageId);

    @Modifying
    @Query(value = "delete from person_marriage where person_id = :personId and marriage_id = :marriageId", nativeQuery = true)
    void deletePersonMarriageLinkByMarriageIdPersonId(@Param("marriageId") Long marriageId, @Param("personId") Long personId);

    @Modifying
    @Query(value = "insert into person_marriage (person_id, marriage_id) values (:personId, :marriageId)", nativeQuery = true)
    void insertMarriagePersonLink(@Param("marriageId") Long marriageId, @Param("personId") Long personId);

    @Modifying
    @Query(value = "insert into person_marriage (person_id, marriage_id) values (:personId, :marriageId)", nativeQuery = true)
    void insertPersonMarriageLink(@Param("personId") Long personId, @Param("marriageId") Long marriageId);

    @Query("select m from Marriage m left join fetch m.witnesses left join fetch m.witnesses.locality left join fetch m.witnesses.locality.anotherNames " +
            "where m.id = :id")
    @EntityGraph(value = "Marriage.withArchiveDocument", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Marriage> findWithWitnessesAndArchiveDocument(@Param("id") Long id);

    @Query("select m from Marriage m where m.id = :id")
    @EntityGraph(value = "Marriage.withHusbandLocality", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Marriage> findWithHusbandLocality(@Param("id") Long id);

    @Query("select m from Marriage m where m.id = :id")
    @EntityGraph(value = "Marriage.withWifeLocality", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Marriage> findWithWifeLocality(@Param("id") Long id);

    @Query("select m from Marriage m where m.id = :id")
    @EntityGraph(value = "Marriage.withPersons", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Marriage> findWithPersons(@Param("id") Long id);

    @Modifying
    @Query(value = "update marriage set husband_locality_id = :newHusbandLocalityId where husband_locality_id = :husbandLocalityId", nativeQuery = true)
    void updateHusbandLocalityId(@Param("husbandLocalityId") Long birthLocalityId, @Param("newHusbandLocalityId") Long newHusbandLocalityId);

    @Modifying
    @Query(value = "update marriage set wife_locality_id = :newWifeLocalityId where wife_locality_id = :wifeLocalityId", nativeQuery = true)
    void updateWifeLocalityId(@Param("wifeLocalityId") Long wifeLocalityId, @Param("newWifeLocalityId") Long newWifeLocalityId);

    @Modifying
    @Query(value = "update marriage set husband_locality_id = :newHusbandLocalityId where id = :id", nativeQuery = true)
    void updateHusbandLocalityIdById(@Param("id") Long id, @Param("newHusbandLocalityId") Long newHusbandLocalityId);

    @Modifying
    @Query(value = "update marriage set wife_locality_id = :newWifeLocalityId where id = :id", nativeQuery = true)
    void updateWifeLocalityIdById(@Param("id") Long id, @Param("newWifeLocalityId") Long newWifeLocalityId);

    @Modifying
    @Query(value = "update witness set locality_id = :newLocalityId where locality_id = :localityId", nativeQuery = true)
    void updateWitnessLocalityId(@Param("localityId") Long localityId, @Param("newLocalityId") Long newLocalityId);

    @Modifying
    @Query(value = "delete from witness where marriage_id = :id", nativeQuery = true)
    void deleteWitnessesById(@Param("id") Long id);

    @Modifying
    @Query(value = "insert into witness (marriage_id, last_name, name, status, surname, locality_id, witness_type) " +
            "values (:marriageId, :#{#entity.fullName?.lastName}, :#{#entity.fullName?.name}, :#{#entity.fullName?.status}, " +
            ":#{#entity.fullName?.surname}, :#{#entity.locality?.id}, :#{#entity.witnessType?.name})",
            nativeQuery = true)
    void insertWitness(@Param("marriageId") Long marriageId, @Param("entity") Witness entity);
}
