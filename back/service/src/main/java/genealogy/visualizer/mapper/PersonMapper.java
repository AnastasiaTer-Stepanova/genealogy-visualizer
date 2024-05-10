package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Person;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface PersonMapper {

    @Mapping(target = "christening", ignore = true)
    @Mapping(target = "familyRevision", ignore = true)
    @Mapping(target = "marriages", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"fullName"})
    genealogy.visualizer.entity.Person toEntity(Person person);

    @Mapping(target = "fullName", ignore = true)
    @BeanMapping(ignoreUnmappedSourceProperties = {"christening", "familyRevision", "marriages"})
    Person toDTO(genealogy.visualizer.entity.Person person);

}
