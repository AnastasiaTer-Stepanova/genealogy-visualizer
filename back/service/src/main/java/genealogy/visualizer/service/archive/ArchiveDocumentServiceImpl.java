package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.EasyArchiveDocumentMapper;
import genealogy.visualizer.service.ArchiveDocumentDAO;

import java.util.List;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

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
        genealogy.visualizer.entity.ArchiveDocument entity = archiveDocumentDAO.findFullInfoById(id);
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return archiveDocumentMapper.toDTO(entity);
    }

    @Override
    public ArchiveDocument save(ArchiveDocument archiveDocument) {
        return archiveDocumentMapper.toDTO(archiveDocumentDAO.save(archiveDocumentMapper.toEntity(archiveDocument)));
    }

    @Override
    public ArchiveDocument update(ArchiveDocument archiveDocument) {
        genealogy.visualizer.entity.ArchiveDocument entity = archiveDocumentDAO.update(archiveDocumentMapper.toEntity(archiveDocument));
        if (entity == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        return archiveDocumentMapper.toDTO(entity);
    }

    @Override
    public List<EasyArchiveDocument> filter(ArchiveDocumentFilter filter) {
        return easyArchiveDocumentMapper.toDTOs(archiveDocumentDAO.filter(archiveDocumentMapper.toFilterDTO(filter)));
    }
}
