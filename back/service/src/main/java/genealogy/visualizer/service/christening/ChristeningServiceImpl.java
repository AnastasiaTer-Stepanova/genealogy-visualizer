package genealogy.visualizer.service.christening;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.dto.ChristeningFilterDTO;
import genealogy.visualizer.mapper.ChristeningMapper;
import genealogy.visualizer.mapper.EasyChristeningMapper;
import genealogy.visualizer.service.AbstractCommonOperationService;
import genealogy.visualizer.service.ChristeningDAO;

import java.util.List;

public class ChristeningServiceImpl extends AbstractCommonOperationService<genealogy.visualizer.entity.Christening, Christening, ChristeningFilter, EasyChristening, ChristeningFilterDTO>
        implements ChristeningService {

    public ChristeningServiceImpl(ChristeningDAO christeningDAO,
                                  ChristeningMapper christeningMapper,
                                  EasyChristeningMapper easyChristeningMapper) {
        super(christeningDAO, christeningDAO, christeningMapper, christeningMapper, easyChristeningMapper);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public Christening getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Christening save(Christening christening) {
        return super.save(christening);
    }

    @Override
    public Christening update(Christening christening) {
        return super.update(christening);
    }

    @Override
    public List<EasyChristening> filter(ChristeningFilter filter) {
        return super.filter(filter);
    }
}
