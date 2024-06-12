package genealogy.visualizer.service.christening;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.mapper.ChristeningMapper;
import genealogy.visualizer.mapper.EasyChristeningMapper;
import genealogy.visualizer.service.ChristeningDAO;

import java.util.List;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

public class ChristeningServiceImpl implements ChristeningService {

    private final ChristeningDAO christeningDAO;
    private final ChristeningMapper christeningMapper;
    private final EasyChristeningMapper easyChristeningMapper;

    public ChristeningServiceImpl(ChristeningDAO christeningDAO,
                                  ChristeningMapper christeningMapper,
                                  EasyChristeningMapper easyChristeningMapper) {
        this.christeningDAO = christeningDAO;
        this.christeningMapper = christeningMapper;
        this.easyChristeningMapper = easyChristeningMapper;
    }

    @Override
    public void delete(Long id) {
        christeningDAO.delete(id);
    }

    @Override
    public Christening getById(Long id) {
        genealogy.visualizer.entity.Christening entity = christeningDAO.findFullInfoById(id);
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return christeningMapper.toDTO(entity);
    }

    @Override
    public Christening save(Christening christening) {
        return christeningMapper.toDTO(christeningDAO.save(christeningMapper.toEntity(christening)));
    }

    @Override
    public Christening update(Christening christening) {
        genealogy.visualizer.entity.Christening entity = christeningDAO.update(christeningMapper.toEntity(christening));
        if (entity == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        return christeningMapper.toDTO(entity);
    }

    @Override
    public List<EasyChristening> filter(ChristeningFilter filter) {
        return easyChristeningMapper.toDTOs(christeningDAO.filter(christeningMapper.toFilter(filter)));
    }
}
