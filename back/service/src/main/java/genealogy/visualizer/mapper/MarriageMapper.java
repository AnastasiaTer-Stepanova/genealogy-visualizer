package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        unmappedSourcePolicy = ReportingPolicy.ERROR,
        uses = {AgeMapper.class,
                EasyArchiveDocumentMapper.class,
                EasyPersonMapper.class,
                FullNameMapper.class,
                LocalityMapper.class,
                WitnessMapper.class})
public interface MarriageMapper extends CommonMapper {

    genealogy.visualizer.entity.Marriage toEntity(Marriage marriage);

    Marriage toDTO(genealogy.visualizer.entity.Marriage marriage);

    genealogy.visualizer.dto.MarriageFilterDTO toFilter(MarriageFilter filter);

}
