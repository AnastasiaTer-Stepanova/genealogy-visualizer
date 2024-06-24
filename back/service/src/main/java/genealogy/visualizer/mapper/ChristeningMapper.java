package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.dto.ChristeningFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {EasyArchiveDocumentMapper.class,
                EasyPersonMapper.class,
                EasyLocalityMapper.class,
                FullNameMapper.class,
                GodParentMapper.class})
public interface ChristeningMapper extends CommonMapper<Christening, genealogy.visualizer.entity.Christening>, FilterMapper<ChristeningFilterDTO, ChristeningFilter> {

    @Mapping(target = "findWithHavePerson", source = "isFindWithHavePerson")
    genealogy.visualizer.dto.ChristeningFilterDTO toFilter(ChristeningFilter filter);
}
