package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.dto.DeathFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {EasyArchiveDocumentMapper.class,
                EasyPersonMapper.class,
                EasyLocalityMapper.class,
                FullNameMapper.class})
public interface DeathMapper extends CommonMapper<Death, genealogy.visualizer.entity.Death>, FilterMapper<DeathFilterDTO, DeathFilter> {

    @Mapping(target = "findWithHavePerson", source = "isFindWithHavePerson")
    DeathFilterDTO toFilter(DeathFilter filter);

}
