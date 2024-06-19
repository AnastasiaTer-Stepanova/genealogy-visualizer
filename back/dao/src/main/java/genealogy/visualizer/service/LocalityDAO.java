package genealogy.visualizer.service;

import genealogy.visualizer.dto.LocalityFilterDTO;
import genealogy.visualizer.entity.Locality;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface LocalityDAO {

    void delete(Long id) throws IllegalArgumentException;

    Locality save(Locality locality) throws IllegalArgumentException, EmptyResultDataAccessException;

    Locality update(Locality locality) throws IllegalArgumentException, EmptyResultDataAccessException;

    Locality findFullInfoById(Long id) throws EmptyResultDataAccessException;

    List<Locality> filter(LocalityFilterDTO filter) throws EmptyResultDataAccessException;

}
