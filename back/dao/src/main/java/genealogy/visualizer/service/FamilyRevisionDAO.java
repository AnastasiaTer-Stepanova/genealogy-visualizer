package genealogy.visualizer.service;

import genealogy.visualizer.dto.FamilyRevisionFilterDTO;
import genealogy.visualizer.entity.FamilyRevision;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface FamilyRevisionDAO {

    void saveBatch(List<FamilyRevision> familyRevisions);

    void delete(Long id) throws IllegalArgumentException;

    FamilyRevision save(FamilyRevision familyRevision) throws IllegalArgumentException, EmptyResultDataAccessException;

    FamilyRevision update(FamilyRevision familyRevision) throws IllegalArgumentException, EmptyResultDataAccessException;

    FamilyRevision findFullInfoById(Long id) throws EmptyResultDataAccessException;

    List<FamilyRevision> filter(FamilyRevisionFilterDTO filter) throws EmptyResultDataAccessException;

}
