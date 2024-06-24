package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.api.model.EasyArchiveDocument;
import genealogy.visualizer.dto.ArchiveDocumentFilterDTO;
import genealogy.visualizer.mapper.ArchiveDocumentMapper;
import genealogy.visualizer.mapper.EasyArchiveDocumentMapper;
import genealogy.visualizer.service.AbstractCommonOperationService;
import genealogy.visualizer.service.ArchiveDocumentDAO;

import java.util.List;

public class ArchiveDocumentServiceImpl extends AbstractCommonOperationService<genealogy.visualizer.entity.ArchiveDocument, ArchiveDocument, ArchiveDocumentFilter, EasyArchiveDocument, ArchiveDocumentFilterDTO>
        implements ArchiveDocumentService {

    public ArchiveDocumentServiceImpl(ArchiveDocumentDAO archiveDocumentDAO,
                                      ArchiveDocumentMapper archiveDocumentMapper,
                                      EasyArchiveDocumentMapper easyArchiveDocumentMapper) {
        super(archiveDocumentDAO, archiveDocumentDAO, archiveDocumentMapper, archiveDocumentMapper, easyArchiveDocumentMapper);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public ArchiveDocument getById(Long id) {
        return super.getById(id);
    }

    @Override
    public ArchiveDocument save(ArchiveDocument archiveDocument) {
        return super.save(archiveDocument);
    }

    @Override
    public ArchiveDocument update(ArchiveDocument archiveDocument) {
        return super.update(archiveDocument);
    }

    @Override
    public List<EasyArchiveDocument> filter(ArchiveDocumentFilter filter) {
        return super.filter(filter);
    }
}
