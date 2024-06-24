package genealogy.visualizer.service.archive;

import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveFilter;
import genealogy.visualizer.api.model.EasyArchive;
import genealogy.visualizer.dto.ArchiveFilterDTO;
import genealogy.visualizer.mapper.ArchiveMapper;
import genealogy.visualizer.mapper.EasyArchiveMapper;
import genealogy.visualizer.service.AbstractCommonOperationService;
import genealogy.visualizer.service.ArchiveDAO;

import java.util.List;

public class ArchiveServiceImpl extends AbstractCommonOperationService<genealogy.visualizer.entity.Archive, Archive, ArchiveFilter, EasyArchive, ArchiveFilterDTO>
        implements ArchiveService {

    public ArchiveServiceImpl(ArchiveDAO archiveDAO,
                              ArchiveMapper archiveMapper,
                              EasyArchiveMapper easyArchiveMapper) {
        super(archiveDAO, archiveDAO, archiveMapper, archiveMapper, easyArchiveMapper);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public Archive getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Archive save(Archive archive) {
        return super.save(archive);
    }

    @Override
    public Archive update(Archive archive) {
        return super.update(archive);
    }

    @Override
    public List<EasyArchive> filter(ArchiveFilter filter) {
        return super.filter(filter);
    }
}
