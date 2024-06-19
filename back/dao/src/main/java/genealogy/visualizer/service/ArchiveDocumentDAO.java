package genealogy.visualizer.service;

import genealogy.visualizer.dto.ArchiveDocumentFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public interface ArchiveDocumentDAO {

    void delete(Long id) throws IllegalArgumentException;

    ArchiveDocument save(ArchiveDocument archiveDocument) throws IllegalArgumentException, EmptyResultDataAccessException;

    ArchiveDocument update(ArchiveDocument archiveDocument) throws IllegalArgumentException, EmptyResultDataAccessException;

    ArchiveDocument findFullInfoById(Long id) throws EmptyResultDataAccessException;

    List<ArchiveDocument> filter(ArchiveDocumentFilterDTO filter) throws EmptyResultDataAccessException;

    ArchiveDocument saveOrFindIfExistDocument(ArchiveDocument archiveDocument) throws EmptyResultDataAccessException;

}
