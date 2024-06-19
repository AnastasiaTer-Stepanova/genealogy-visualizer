package genealogy.visualizer.service;

import genealogy.visualizer.dto.ChristeningFilterDTO;
import genealogy.visualizer.entity.Christening;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface ChristeningDAO {

    void delete(Long id) throws IllegalArgumentException;

    Christening save(Christening christening) throws IllegalArgumentException, EmptyResultDataAccessException;

    Christening update(Christening christening) throws IllegalArgumentException, EmptyResultDataAccessException;

    Christening findFullInfoById(Long id) throws EmptyResultDataAccessException;

    List<Christening> filter(ChristeningFilterDTO filter) throws EmptyResultDataAccessException;
}
