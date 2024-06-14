package genealogy.visualizer.service.christening;

import genealogy.visualizer.api.model.Christening;
import genealogy.visualizer.api.model.ChristeningFilter;
import genealogy.visualizer.api.model.EasyChristening;
import genealogy.visualizer.mapper.ChristeningMapper;
import genealogy.visualizer.mapper.EasyChristeningMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.ChristeningDAO;

import java.util.List;
import java.util.Optional;

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
        return Optional.ofNullable(christeningMapper.toDTO(christeningDAO.findFullInfoById(id)))
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Christening save(Christening christening) {
        if (christening == null || christening.getId() != null) {
            throw new BadRequestException("Christening must not have an id");
        }
        return christeningMapper.toDTO(christeningDAO.save(christeningMapper.toEntity(christening)));
    }

    @Override
    public Christening update(Christening christening) {
        if (christening == null || christening.getId() == null) {
            throw new BadRequestException("Christening must have an id");
        }
        genealogy.visualizer.entity.Christening entity = christeningDAO.update(christeningMapper.toEntity(christening));
        if (entity == null) {
            throw new NotFoundException("Christening for update not found");
        }
        return christeningMapper.toDTO(entity);
    }

    @Override
    public List<EasyChristening> filter(ChristeningFilter filter) {
        return Optional.ofNullable(easyChristeningMapper.toDTOs(christeningDAO.filter(christeningMapper.toFilter(filter))))
                .orElseThrow(NotFoundException::new);
    }
}
