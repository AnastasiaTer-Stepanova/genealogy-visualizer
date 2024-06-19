package genealogy.visualizer.service;

import genealogy.visualizer.dto.MarriageFilterDTO;
import genealogy.visualizer.entity.Marriage;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface MarriageDAO {

    void delete(Long id) throws IllegalArgumentException;

    Marriage save(Marriage marriage) throws IllegalArgumentException, EmptyResultDataAccessException;

    Marriage update(Marriage marriage) throws IllegalArgumentException, EmptyResultDataAccessException;

    Marriage findFullInfoById(Long id) throws EmptyResultDataAccessException;

    List<Marriage> filter(MarriageFilterDTO filter) throws EmptyResultDataAccessException;

}
