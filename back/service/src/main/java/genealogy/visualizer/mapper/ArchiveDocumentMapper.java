package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.ArchiveDocument;
import genealogy.visualizer.api.model.ArchiveDocumentFilter;
import genealogy.visualizer.dto.ArchiveDocumentFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {EasyArchiveDocumentMapper.class,
                EasyChristeningMapper.class,
                EasyDeathMapper.class,
                EasyFamilyRevisionMapper.class,
                EasyMarriageMapper.class,
                EasyArchiveMapper.class})
public interface ArchiveDocumentMapper extends CommonMapper<ArchiveDocument, genealogy.visualizer.entity.ArchiveDocument>, FilterMapper<ArchiveDocumentFilterDTO, ArchiveDocumentFilter> {

}
