package genealogy.visualizer.mapper;

import genealogy.visualizer.api.model.Error;
import genealogy.visualizer.api.model.FamilyRevisionErrorResponse;
import genealogy.visualizer.api.model.GenealogyVisualizeErrorResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR, unmappedSourcePolicy = ReportingPolicy.ERROR)
public interface ErrorMapper {

    FamilyRevisionErrorResponse toFamilyRevisionError(Error error);

    GenealogyVisualizeErrorResponse toGenealogyVisualizeError(Error error);

}
