package genealogy.visualizer.service.locality;

import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityFilter;
import genealogy.visualizer.service.CrudService;
import genealogy.visualizer.service.FilterService;

public interface LocalityService extends CrudService<Locality>, FilterService<EasyLocality, LocalityFilter> {
}
