package genealogy.visualizer.service.marriage;

import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;
import genealogy.visualizer.mapper.EasyMarriageMapper;
import genealogy.visualizer.mapper.MarriageMapper;
import genealogy.visualizer.service.MarriageDAO;

import java.util.List;

import static genealogy.visualizer.service.util.ErrorHelper.BAD_REQUEST_ERROR;
import static genealogy.visualizer.service.util.ErrorHelper.NOT_FOUND_ERROR;

public class MarriageServiceImpl implements MarriageService {

    private final MarriageDAO marriageDAO;
    private final MarriageMapper marriageMapper;
    private final EasyMarriageMapper easyMarriageMapper;

    public MarriageServiceImpl(MarriageDAO marriageDAO,
                               MarriageMapper marriageMapper,
                               EasyMarriageMapper easyMarriageMapper) {
        this.marriageDAO = marriageDAO;
        this.marriageMapper = marriageMapper;
        this.easyMarriageMapper = easyMarriageMapper;
    }

    @Override
    public void delete(Long id) {
        marriageDAO.delete(id);
    }

    @Override
    public Marriage getById(Long id) {
        genealogy.visualizer.entity.Marriage entity = marriageDAO.findFullInfoById(id);
        if (entity == null) {
            throw new RuntimeException(NOT_FOUND_ERROR);
        }
        return marriageMapper.toDTO(entity);
    }

    @Override
    public Marriage save(Marriage marriage) {
        return marriageMapper.toDTO(marriageDAO.save(marriageMapper.toEntity(marriage)));
    }

    @Override
    public Marriage update(Marriage marriage) {
        genealogy.visualizer.entity.Marriage entity = marriageDAO.update(marriageMapper.toEntity(marriage));
        if (entity == null) {
            throw new RuntimeException(BAD_REQUEST_ERROR);
        }
        return marriageMapper.toDTO(entity);
    }

    @Override
    public List<EasyMarriage> filter(MarriageFilter filter) {
        return easyMarriageMapper.toDTOs(marriageDAO.filter(marriageMapper.toFilter(filter)));
    }
}
