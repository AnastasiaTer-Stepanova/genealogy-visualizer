package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;

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
public interface DeathMapper extends CommonMapper {

    genealogy.visualizer.entity.Death toEntity(Death death);

    Death toDTO(genealogy.visualizer.entity.Death death);

    @Mapping(target = "findWithHavePerson", source = "isFindWithHavePerson")
    genealogy.visualizer.dto.DeathFilterDTO toFilter(DeathFilter filter);

}
