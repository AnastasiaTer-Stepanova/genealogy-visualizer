package genealogy.visualizer.service.locality;

import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityFilter;
import genealogy.visualizer.mapper.EasyLocalityMapper;
import genealogy.visualizer.mapper.LocalityMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.LocalityDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public class LocalityServiceImpl implements LocalityService {

    private final LocalityDAO localityDAO;
    private final LocalityMapper localityMapper;
    private final EasyLocalityMapper easyLocalityMapper;

    public LocalityServiceImpl(LocalityDAO localityDAO,
                               LocalityMapper localityMapper,
                               EasyLocalityMapper easyLocalityMapper) {
        this.localityDAO = localityDAO;
        this.localityMapper = localityMapper;
        this.easyLocalityMapper = easyLocalityMapper;
    }

    @Override
    public void delete(Long id) {
        localityDAO.delete(id);
    }

    @Override
    public Locality getById(Long id) {
        try {
            return localityMapper.toDTO(localityDAO.findFullInfoById(id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public Locality save(Locality locality) {
        if (locality == null || locality.getId() != null) {
            throw new BadRequestException("Locality must not have an id");
        }
        return localityMapper.toDTO(localityDAO.save(localityMapper.toEntity(locality)));
    }

    @Override
    public Locality update(Locality locality) {
        if (locality == null || locality.getId() == null) {
            throw new BadRequestException("Locality must have an id");
        }
        genealogy.visualizer.entity.Locality entity;
        try {
            entity = localityDAO.update(localityMapper.toEntity(locality));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Locality for update not found");
        }
        return localityMapper.toDTO(entity);
    }

    @Override
    public List<EasyLocality> filter(LocalityFilter filter) {
        try {
            return easyLocalityMapper.toDTOs(localityDAO.filter(localityMapper.toFilter(filter)));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Localities by filter not found");
        }
    }
}
