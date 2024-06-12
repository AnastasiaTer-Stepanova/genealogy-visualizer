package genealogy.visualizer.service.locality;

import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityFilter;

import java.util.List;

public interface LocalityService {

    void delete(Long id);

    Locality getById(Long id);

    Locality save(Locality death);

    Locality update(Locality death);

    List<EasyLocality> filter(LocalityFilter filter);
}
