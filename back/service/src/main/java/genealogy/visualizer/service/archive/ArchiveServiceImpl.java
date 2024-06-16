package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveFilter;
import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.mapper.ArchiveMapper;
import genealogy.visualizer.mapper.EasyArchiveMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.ArchiveDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;
import java.util.Optional;

public class ArchiveServiceImpl implements ArchiveService {

    private final ArchiveDAO archiveDAO;
    private final ArchiveMapper archiveMapper;
    private final EasyArchiveMapper easyArchiveMapper;

    public ArchiveServiceImpl(ArchiveDAO archiveDAO, ArchiveMapper archiveMapper, EasyArchiveMapper easyArchiveMapper) {
        this.archiveDAO = archiveDAO;
        this.archiveMapper = archiveMapper;
        this.easyArchiveMapper = easyArchiveMapper;
    }

    @Override
    public void delete(Long id) {
        archiveDAO.delete(id);
    }

    @Override
    public Archive getById(Long id) {
        try {
            return archiveMapper.toDTO(archiveDAO.findFullInfoById(id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public Archive save(Archive archive) {
        if (archive == null || archive.getId() != null) {
            throw new BadRequestException("Archive must not have an id");
        }
        return archiveMapper.toDTO(archiveDAO.save(archiveMapper.toEntity(archive)));
    }

    @Override
    public Archive update(Archive archive) {
        if (archive == null || archive.getId() == null) {
            throw new BadRequestException("Archive must have an id");
        }
        genealogy.visualizer.entity.Archive entity;
        try {
            entity = archiveDAO.update(archiveMapper.toEntity(archive));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Archive for update not found");
        }
        return archiveMapper.toDTO(entity);
    }

    @Override
    public List<EasyArchive> filter(ArchiveFilter filter) {
        return Optional.ofNullable(easyArchiveMapper.toDTOs(archiveDAO.filter(archiveMapper.toFilterDTO(filter))))
                .orElseThrow(NotFoundException::new);
    }
}
