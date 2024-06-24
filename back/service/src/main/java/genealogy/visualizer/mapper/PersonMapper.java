package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Person;
import genealogy.visualizer.api.model.PersonFilter;
import genealogy.visualizer.dto.PersonFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        uses = {EasyChristeningMapper.class,
                EasyDeathMapper.class,
                EasyFamilyRevisionMapper.class,
                EasyMarriageMapper.class,
                EasyPersonMapper.class,
                FullNameMapper.class,
                EasyLocalityMapper.class})
public interface PersonMapper extends CommonMapper<Person, genealogy.visualizer.entity.Person>, FilterMapper<PersonFilterDTO, PersonFilter> {

    @Mapping(target = "graphs", ignore = true)
    PersonFilterDTO toFilter(PersonFilter filter);

}
