package genealogy.visualizer.service.death;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.api.model.EasyDeath;

import java.util.List;

public interface DeathService {

    void delete(Long id);

    Death getById(Long id);

    Death save(Death death);

    Death update(Death death);

    List<EasyDeath> filter(DeathFilter filter);
}
