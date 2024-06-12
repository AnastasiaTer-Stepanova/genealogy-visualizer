package genealogy.visualizer.service.locality;

import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityFilter;
import genealogy.visualizer.mapper.EasyLocalityMapper;
import genealogy.visualizer.mapper.LocalityMapper;
import genealogy.visualizer.service.LocalityDAO;

import java.util.List;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

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
        genealogy.visualizer.entity.Locality entity = localityDAO.findFullInfoById(id);
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return localityMapper.toDTO(entity);
    }

    @Override
    public Locality save(Locality locality) {
        return localityMapper.toDTO(localityDAO.save(localityMapper.toEntity(locality)));
    }

    @Override
    public Locality update(Locality locality) {
        genealogy.visualizer.entity.Locality entity = localityDAO.update(localityMapper.toEntity(locality));
        if (entity == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        return localityMapper.toDTO(entity);
    }

    @Override
    public List<EasyLocality> filter(LocalityFilter filter) {
        return easyLocalityMapper.toDTOs(localityDAO.filter(localityMapper.toFilter(filter)));
    }
}
