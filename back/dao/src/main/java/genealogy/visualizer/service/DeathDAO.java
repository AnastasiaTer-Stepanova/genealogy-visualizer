package genealogy.visualizer.service;

import genealogy.visualizer.dto.DeathFilterDTO;
import genealogy.visualizer.entity.Death;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface DeathDAO {

    void delete(Long id) throws IllegalArgumentException;

    Death save(Death death) throws IllegalArgumentException, EmptyResultDataAccessException;

    Death update(Death death) throws IllegalArgumentException, EmptyResultDataAccessException;

    Death findFullInfoById(Long id) throws EmptyResultDataAccessException;

    List<Death> filter(DeathFilterDTO filter) throws EmptyResultDataAccessException;
}
