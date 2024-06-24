package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Archive;
import genealogy.visualizer.api.model.ArchiveFilter;
import genealogy.visualizer.dto.ArchiveFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {EasyArchiveDocumentMapper.class})
public interface ArchiveMapper extends CommonMapper<Archive, genealogy.visualizer.entity.Archive>, FilterMapper<ArchiveFilterDTO, ArchiveFilter> {
}
