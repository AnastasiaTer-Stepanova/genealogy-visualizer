package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyPerson;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class})
public interface EasyPersonMapper {

    @Mapping(target = "birthLocality", ignore = true)
    @Mapping(target = "deathLocality", ignore = true)
    @Mapping(target = "partners", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "parents", ignore = true)
    @Mapping(target = "christening", ignore = true)
    @Mapping(target = "death", ignore = true)
    @Mapping(target = "revisions", ignore = true)
    @Mapping(target = "marriages", ignore = true)
    genealogy.visualizer.entity.Person toEntity(EasyPerson person);

    @BeanMapping(ignoreUnmappedSourceProperties = {"birthLocality", "deathLocality", "partners", "children", "parents",
            "christening", "death", "revisions", "marriages"})
    EasyPerson toDTO(genealogy.visualizer.entity.Person person);

    Set<EasyPerson> toDTOs(List<genealogy.visualizer.entity.Person> person);

}
