package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.EasyArchiveDocumentMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.ArchiveDocumentDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

public class ArchiveDocumentServiceImpl implements ArchiveDocumentService {

    private final ArchiveDocumentDAO archiveDocumentDAO;
    private final ArchiveDocumentMapper archiveDocumentMapper;
    private final EasyArchiveDocumentMapper easyArchiveDocumentMapper;

    public ArchiveDocumentServiceImpl(ArchiveDocumentDAO archiveDocumentDAO, ArchiveDocumentMapper archiveDocumentMapper, EasyArchiveDocumentMapper easyArchiveDocumentMapper) {
        this.archiveDocumentDAO = archiveDocumentDAO;
        this.archiveDocumentMapper = archiveDocumentMapper;
        this.easyArchiveDocumentMapper = easyArchiveDocumentMapper;
    }

    @Override
    public void delete(Long id) {
        archiveDocumentDAO.delete(id);
    }

    @Override
    public ArchiveDocument getById(Long id) {
        try {
            return archiveDocumentMapper.toDTO(archiveDocumentDAO.findFullInfoById(id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public ArchiveDocument save(ArchiveDocument archiveDocument) {
        if (archiveDocument == null || archiveDocument.getId() != null) {
            throw new BadRequestException("Archive document must not have an id");
        }
        return archiveDocumentMapper.toDTO(archiveDocumentDAO.save(archiveDocumentMapper.toEntity(archiveDocument)));
    }

    @Override
    public ArchiveDocument update(ArchiveDocument archiveDocument) {
        if (archiveDocument == null || archiveDocument.getId() == null) {
            throw new BadRequestException("Archive document must have an id");
        }
        genealogy.visualizer.entity.ArchiveDocument entity;
        try {
            entity = archiveDocumentDAO.update(archiveDocumentMapper.toEntity(archiveDocument));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Archive document for update not found");
        }
        return archiveDocumentMapper.toDTO(entity);
    }

    @Override
    public List<EasyArchiveDocument> filter(ArchiveDocumentFilter filter) {
        return Optional.ofNullable(easyArchiveDocumentMapper.toDTOs(archiveDocumentDAO.filter(archiveDocumentMapper.toFilterDTO(filter))))
                .orElseThrow(NotFoundException::new);
    }
}
