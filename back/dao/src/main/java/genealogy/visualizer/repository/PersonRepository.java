package genealogy.visualizer.repository;

import genealogy.visualizer.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {

    @Query(value = "update person set " +
            "birth_date = :#{#entity.birthDate?.date}, birth_date_range_type = :#{#entity.birthDate?.dateRangeType.name}, " +
            "death_date = :#{#entity.deathDate?.date}, death_date_range_type = :#{#entity.deathDate?.dateRangeType.name}, " +
            "last_name = :#{#entity.fullName?.lastName}, name = :#{#entity.fullName?.name}, status = :#{#entity.fullName?.status}, " +
            "surname = :#{#entity.fullName?.surname}, birth_locality_id = :#{#entity.birthLocality?.id}, death_locality_id = :#{#entity.deathLocality?.id}, " +
            "sex = :#{#entity.sex?.name} " +
            "where id = :#{#entity.id} returning *", nativeQuery = true)
    Person update(@Param("entity") Person entity);

    @Query("select p from Person p left join fetch p.marriages where p.id = :id ")
    Optional<Person> findByIdWithMarriages(@Param("id") Long id);

    @Query("select p from Person p left join fetch p.children where p.id = :id ")
    Optional<Person> findByIdWithChildren(@Param("id") Long id);

    @Query("select p from Person p left join fetch p.partners where p.id = :id ")
    Optional<Person> findByIdWithPartners(@Param("id") Long id);

    @Query("select p from Person p left join fetch p.parents where p.id = :id ")
    Optional<Person> findByIdWithParents(@Param("id") Long id);

    @Query("select p from Person p left join fetch p.death left join fetch p.christening left join fetch p.birthLocality " +
            "left join fetch p.deathLocality left join fetch p.revisions left join fetch p.christening.archiveDocument " +
            "left join fetch p.christening.archiveDocument.archive where p.id = :id ")
    Optional<Person> findFullInfoById(@Param("id") Long id);

    @Modifying
    @Query(value = "delete from person_parent where parent_id = :parentId and person_id = :personId", nativeQuery = true)
    void deleteParentLinkByPersonIdAndParentId(@Param("parentId") Long parentId, @Param("personId") Long personId);

    @Modifying
    @Query(value = "delete from person_partner where partner_id = :partnerId and person_id = :personId", nativeQuery = true)
    void deletePartnerLinkByPersonIdAndPartnerId(@Param("partnerId") Long partnerId, @Param("personId") Long personId);

    @Modifying
    @Query(value = "delete from person_parent where parent_id = :id or person_id = :id", nativeQuery = true)
    void deleteParentLinkById(@Param("id") Long id);

    @Modifying
    @Query(value = "delete from person_partner where partner_id = :id or person_id = :id", nativeQuery = true)
    void deletePartnerLinkById(@Param("id") Long id);

    @Modifying
    @Query(value = "delete from person_marriage where person_id = :id", nativeQuery = true)
    void deleteMarriageLinkById(@Param("id") Long id);

    @Modifying
    @Query(value = "update person set birth_locality_id = :newBirthLocalityId where birth_locality_id = :birthLocalityId", nativeQuery = true)
    void updateBirthLocalityId(@Param("birthLocalityId") Long birthLocalityId, @Param("newBirthLocalityId") Long newBirthLocalityId);

    @Modifying
    @Query(value = "update person set death_locality_id = :newDeathLocalityId where death_locality_id = :deathLocalityId", nativeQuery = true)
    void updateDeathLocalityId(@Param("deathLocalityId") Long deathLocalityId, @Param("newDeathLocalityId") Long newDeathLocalityId);

    @Modifying
    @Query(value = "update person set birth_locality_id = :newBirthLocalityId where id = :id", nativeQuery = true)
    void updateBirthLocalityIdById(@Param("id") Long id, @Param("newBirthLocalityId") Long newBirthLocalityId);

    @Modifying
    @Query(value = "update person set death_locality_id = :newDeathLocalityId where id = :id", nativeQuery = true)
    void updateDeathLocalityIdById(@Param("id") Long id, @Param("newDeathLocalityId") Long newDeathLocalityId);

    @Modifying
    @Query(value = "insert into person_partner (person_id, partner_id) " +
            "select :personId, :partnerId " +
            "where not exists (select 1 from person_partner where person_id = :personId and partner_id = :partnerId)",
            nativeQuery = true)
    void insertPartnerPersonLink(@Param("partnerId") Long partnerId, @Param("personId") Long personId);

    @Modifying
    @Query(value = "insert into person_parent (person_id, parent_id) values (:personId, :parentId)", nativeQuery = true)
    void insertParentPersonLink(@Param("parentId") Long parentId, @Param("personId") Long personId);

    @Modifying
    @Query(value = "insert into person_parent (person_id, parent_id) values (:childId, :personId)", nativeQuery = true)
    void insertChildPersonLink(@Param("childId") Long childId, @Param("personId") Long personId);

    @Modifying
    @Query(value = "delete from person_parent where person_id = :personId and parent_id = :childId", nativeQuery = true)
    void deleteParentLinkByPersonIdAndChildId(@Param("personId") Long personId, @Param("childId") Long childId);

}
