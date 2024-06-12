package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyDeath;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {AgeMapper.class,
                FullNameMapper.class})
public interface EasyDeathMapper extends CommonMapper {

    @Mapping(target = "locality", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    @Mapping(target = "person", ignore = true)
    genealogy.visualizer.entity.Death toEntity(EasyDeath death);

    @BeanMapping(ignoreUnmappedSourceProperties = {"locality", "archiveDocument", "person"})
    EasyDeath toDTO(genealogy.visualizer.entity.Death death);

    List<EasyDeath> toDTOs(List<genealogy.visualizer.entity.Death> death);

    List<genealogy.visualizer.entity.Death> toEntities(List<EasyDeath> christening);

}
