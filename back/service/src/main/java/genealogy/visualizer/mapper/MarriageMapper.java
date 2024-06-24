package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;
import genealogy.visualizer.dto.MarriageFilterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {AgeMapper.class,
                EasyArchiveDocumentMapper.class,
                EasyPersonMapper.class,
                FullNameMapper.class,
                EasyLocalityMapper.class,
                WitnessMapper.class})
public interface MarriageMapper extends CommonMapper<Marriage, genealogy.visualizer.entity.Marriage>, FilterMapper<MarriageFilterDTO, MarriageFilter> {

    @Mapping(target = "findWithHavePerson", source = "isFindWithHavePerson")
    MarriageFilterDTO toFilter(MarriageFilter filter);

}
