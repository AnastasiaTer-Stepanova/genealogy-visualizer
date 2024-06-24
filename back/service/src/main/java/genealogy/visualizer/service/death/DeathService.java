package genealogy.visualizer.service.death;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.service.CrudService;
import genealogy.visualizer.service.FilterService;

public interface DeathService extends CrudService<Death>, FilterService<EasyDeath, DeathFilter> {
}
