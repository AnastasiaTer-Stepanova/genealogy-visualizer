package genealogy.visualizer.service.marriage;

import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;
import genealogy.visualizer.service.CrudService;
import genealogy.visualizer.service.FilterService;

public interface MarriageService extends CrudService<Marriage>, FilterService<EasyMarriage, MarriageFilter> {
}
