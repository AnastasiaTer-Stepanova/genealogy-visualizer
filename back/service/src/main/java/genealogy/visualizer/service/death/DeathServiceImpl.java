package genealogy.visualizer.service.death;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.dto.DeathFilterDTO;
import genealogy.visualizer.mapper.DeathMapper;
import genealogy.visualizer.mapper.EasyDeathMapper;
import genealogy.visualizer.service.AbstractCommonOperationService;
import genealogy.visualizer.service.DeathDAO;

import java.util.List;

public class DeathServiceImpl extends AbstractCommonOperationService<genealogy.visualizer.entity.Death, Death, DeathFilter, EasyDeath, DeathFilterDTO>
        implements DeathService {

    public DeathServiceImpl(DeathDAO deathDAO,
                            DeathMapper deathMapper,
                            EasyDeathMapper easyDeathMapper) {
        super(deathDAO, deathDAO, deathMapper, deathMapper, easyDeathMapper);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public Death getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Death save(Death death) {
        return super.save(death);
    }

    @Override
    public Death update(Death death) {
        return super.update(death);
    }

    @Override
    public List<EasyDeath> filter(DeathFilter filter) {
        return super.filter(filter);
    }
}
