package genealogy.visualizer.service.christening;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.EasyChristening;

import java.util.List;

public interface ChristeningService {

    void delete(Long id);

    Christening getById(Long id);

    Christening save(Christening christening);

    Christening update(Christening christening);

    List<EasyChristening> filter(ChristeningFilter filter);
}
