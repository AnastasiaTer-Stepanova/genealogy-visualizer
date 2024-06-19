package genealogy.visualizer.service.death;

import genealogy.visualizer.api.model.Death;
import genealogy.visualizer.api.model.DeathFilter;
import genealogy.visualizer.api.model.EasyDeath;
import genealogy.visualizer.mapper.DeathMapper;
import genealogy.visualizer.mapper.EasyDeathMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.DeathDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

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
        try {
            return deathMapper.toDTO(deathDAO.findFullInfoById(id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public Death save(Death death) {
        if (death == null || death.getId() != null) {
            throw new BadRequestException("Death must not have an id");
        }
        return deathMapper.toDTO(deathDAO.save(deathMapper.toEntity(death)));
    }

    @Override
    public Death update(Death death) {
        if (death == null || death.getId() == null) {
            throw new BadRequestException("Archive must have an id");
        }
        genealogy.visualizer.entity.Death entity;
        try {
            entity = deathDAO.update(deathMapper.toEntity(death));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Death for update not found");
        }
        return deathMapper.toDTO(entity);
    }

    @Override
    public List<EasyDeath> filter(DeathFilter filter) {
        try {
            return easyDeathMapper.toDTOs(deathDAO.filter(deathMapper.toFilter(filter)));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Deaths by filter not found");
        }
    }
}
