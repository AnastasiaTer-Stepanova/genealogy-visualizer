package genealogy.visualizer.service;

import genealogy.visualizer.dto.ArchiveDocumentFilterDTO;
import genealogy.visualizer.entity.ArchiveDocument;
import org.springframework.dao.EmptyResultDataAccessException;

public interface ArchiveDocumentDAO extends CrudDAO<ArchiveDocument>, FilterDAO<ArchiveDocument, ArchiveDocumentFilterDTO> {

    ArchiveDocument saveOrFindIfExistDocument(ArchiveDocument archiveDocument) throws EmptyResultDataAccessException;

}
