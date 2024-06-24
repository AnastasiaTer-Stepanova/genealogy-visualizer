package genealogy.visualizer.service;

import genealogy.visualizer.dto.EntityFilter;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface FilterDAO<E, FE extends EntityFilter> {

    List<E> filter(FE filter) throws EmptyResultDataAccessException;

}
