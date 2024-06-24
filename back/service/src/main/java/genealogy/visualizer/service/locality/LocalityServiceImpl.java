package genealogy.visualizer.service.locality;

import genealogy.visualizer.api.model.EasyLocality;
import genealogy.visualizer.api.model.Locality;
import genealogy.visualizer.api.model.LocalityFilter;
import genealogy.visualizer.dto.LocalityFilterDTO;
import genealogy.visualizer.mapper.EasyLocalityMapper;
import genealogy.visualizer.mapper.LocalityMapper;
import genealogy.visualizer.service.AbstractCommonOperationService;
import genealogy.visualizer.service.LocalityDAO;

import java.util.List;

public class LocalityServiceImpl extends AbstractCommonOperationService<genealogy.visualizer.entity.Locality, Locality, LocalityFilter, EasyLocality, LocalityFilterDTO>
        implements LocalityService {

    public LocalityServiceImpl(LocalityDAO localityDAO,
                               LocalityMapper localityMapper,
                               EasyLocalityMapper easyLocalityMapper) {
        super(localityDAO, localityDAO, localityMapper, localityMapper, easyLocalityMapper);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public Locality getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Locality save(Locality locality) {
        return super.save(locality);
    }

    @Override
    public Locality update(Locality locality) {
        return super.update(locality);
    }

    @Override
    public List<EasyLocality> filter(LocalityFilter filter) {
        return super.filter(filter);
    }
}
