package genealogy.visualizer.service.marriage;

import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;

import java.util.List;

public interface MarriageService {

    void delete(Long id);

    Marriage getById(Long id);

    Marriage save(Marriage marriage);

    Marriage update(Marriage marriage);

    List<EasyMarriage> filter(MarriageFilter filter);
}
