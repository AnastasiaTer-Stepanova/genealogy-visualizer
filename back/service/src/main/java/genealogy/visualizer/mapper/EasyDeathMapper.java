package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.entity.Death;
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
public interface EasyDeathMapper extends EasyCommonMapper<EasyDeath, Death> {

    @Mapping(target = "locality", ignore = true)
    @Mapping(target = "archiveDocument", ignore = true)
    @Mapping(target = "person", ignore = true)
    Death toEntity(EasyDeath death);

    @BeanMapping(ignoreUnmappedSourceProperties = {"locality", "archiveDocument", "person"})
    EasyDeath toDTO(Death death);

    List<EasyDeath> toDTOs(List<Death> death);

    List<Death> toEntities(List<EasyDeath> christening);

}
