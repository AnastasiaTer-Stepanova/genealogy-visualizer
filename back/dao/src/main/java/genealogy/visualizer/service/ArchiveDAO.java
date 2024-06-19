package genealogy.visualizer.service;

import genealogy.visualizer.dto.ArchiveFilterDTO;
import genealogy.visualizer.entity.Archive;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface ArchiveDAO {

    void delete(Long id) throws IllegalArgumentException;

    Archive save(Archive archive) throws IllegalArgumentException, EmptyResultDataAccessException;

    Archive update(Archive archive) throws IllegalArgumentException, EmptyResultDataAccessException;

    Archive findFullInfoById(Long id) throws EmptyResultDataAccessException;

    List<Archive> filter(ArchiveFilterDTO filter) throws EmptyResultDataAccessException;

}
