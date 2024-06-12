package genealogy.visualizer.service.death;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.mapper.DeathMapper;
import genealogy.visualizer.mapper.EasyDeathMapper;
import genealogy.visualizer.service.DeathDAO;

import java.util.List;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

public class DeathServiceImpl implements DeathService {

    private final DeathDAO deathDAO;
    private final DeathMapper deathMapper;
    private final EasyDeathMapper easyDeathMapper;

    public DeathServiceImpl(DeathDAO deathDAO,
                            DeathMapper deathMapper,
                            EasyDeathMapper easyDeathMapper) {
        this.deathDAO = deathDAO;
        this.deathMapper = deathMapper;
        this.easyDeathMapper = easyDeathMapper;
    }

    @Override
    public void delete(Long id) {
        deathDAO.delete(id);
    }

    @Override
    public Death getById(Long id) {
        genealogy.visualizer.entity.Death entity = deathDAO.findFullInfoById(id);
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return deathMapper.toDTO(entity);
    }

    @Override
    public Death save(Death death) {
        return deathMapper.toDTO(deathDAO.save(deathMapper.toEntity(death)));
    }

    @Override
    public Death update(Death death) {
        genealogy.visualizer.entity.Death entity = deathDAO.update(deathMapper.toEntity(death));
        if (entity == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        return deathMapper.toDTO(entity);
    }

    @Override
    public List<EasyDeath> filter(DeathFilter filter) {
        return easyDeathMapper.toDTOs(deathDAO.filter(deathMapper.toFilter(filter)));
    }
}
