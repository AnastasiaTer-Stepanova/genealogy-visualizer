package genealogy.visualizer.service.christening;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.service.CrudService;
import genealogy.visualizer.service.FilterService;

public interface ChristeningService extends CrudService<Christening>, FilterService<EasyChristening, ChristeningFilter> {
}
