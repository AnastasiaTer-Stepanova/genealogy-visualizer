package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.EasyPerson;
import genealogy.visualizer.api.model.Person;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {FullNameMapper.class, FamilyRevisionMapper.class, LocalityMapper.class, ChristeningMapper.class,
                DeathMapper.class, MarriageMapper.class})
public interface PersonMapper {

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

    EasyPerson toEasyPersonDTO(genealogy.visualizer.entity.Person person);

    Set<EasyPerson> toEasyPersonDTO(List<genealogy.visualizer.entity.Person> person);

    @Mapping(target = "partners[].partners", ignore = true)
    Person toDTO(genealogy.visualizer.entity.Person person);

    genealogy.visualizer.entity.Person toEntity(Person person);
}
