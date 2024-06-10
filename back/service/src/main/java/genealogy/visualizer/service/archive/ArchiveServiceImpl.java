package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.mapper.ArchiveMapper;
import genealogy.visualizer.service.ArchiveDAO;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveDAO archiveDAO;
    private final ArchiveMapper archiveMapper;

    public ArchiveServiceImpl(ArchiveDAO archiveDAO, ArchiveMapper archiveMapper) {
        this.archiveDAO = archiveDAO;
        this.archiveMapper = archiveMapper;
    }

    @Override
    public void delete(Long id) {
        archiveDAO.delete(id);
    }

    @Override
    public Archive getById(Long id) {
        genealogy.visualizer.entity.Archive entity = archiveDAO.findFullInfoById(id);
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return archiveMapper.toDTO(entity);
    }

    @Override
    public Archive save(Archive archive) {
        return archiveMapper.toDTO(archiveDAO.save(archiveMapper.toEntity(archive)));
    }

    @Override
    public Archive update(Archive archive) {
        genealogy.visualizer.entity.Archive entity = archiveDAO.update(archiveMapper.toEntity(archive));
        if (entity == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        return archiveMapper.toDTO(entity);
    }
}
