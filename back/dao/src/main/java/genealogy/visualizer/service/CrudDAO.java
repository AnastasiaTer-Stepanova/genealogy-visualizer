package genealogy.visualizer.service;

import org.springframework.dao.EmptyResultDataAccessException;

public interface CrudDAO<E> {

    void delete(Long id) throws IllegalArgumentException;

    E save(E entity) throws IllegalArgumentException, EmptyResultDataAccessException;

    E update(E entity) throws IllegalArgumentException, EmptyResultDataAccessException;

    E findFullInfoById(Long id) throws EmptyResultDataAccessException;

}
