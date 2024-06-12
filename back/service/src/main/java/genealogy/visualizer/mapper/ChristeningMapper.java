package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {EasyArchiveDocumentMapper.class,
                EasyPersonMapper.class,
                LocalityMapper.class,
                FullNameMapper.class})
public interface ChristeningMapper extends CommonMapper {

    genealogy.visualizer.entity.Christening toEntity(Christening christening);

    Christening toDTO(genealogy.visualizer.entity.Christening christening);

    genealogy.visualizer.dto.ChristeningFilterDTO toFilter(ChristeningFilter filter);
}
