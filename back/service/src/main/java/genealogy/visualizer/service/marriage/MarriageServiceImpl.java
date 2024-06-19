package genealogy.visualizer.service.marriage;

import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;
import genealogy.visualizer.mapper.EasyMarriageMapper;
import genealogy.visualizer.mapper.MarriageMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import genealogy.visualizer.service.MarriageDAO;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

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
        try {
            return marriageMapper.toDTO(marriageDAO.findFullInfoById(id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public Marriage save(Marriage marriage) {
        if (marriage == null || marriage.getId() != null) {
            throw new BadRequestException("Marriage must not have an id");
        }
        return marriageMapper.toDTO(marriageDAO.save(marriageMapper.toEntity(marriage)));
    }

    @Override
    public Marriage update(Marriage marriage) {
        if (marriage == null || marriage.getId() == null) {
            throw new BadRequestException("Marriage must have an id");
        }
        genealogy.visualizer.entity.Marriage entity;
        try {
            entity = marriageDAO.update(marriageMapper.toEntity(marriage));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Marriage for update not found");
        }
        return marriageMapper.toDTO(entity);
    }

    @Override
    public List<EasyMarriage> filter(MarriageFilter filter) {
        try {
            return easyMarriageMapper.toDTOs(marriageDAO.filter(marriageMapper.toFilter(filter)));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Marriages by filter not found");
        }
    }
}
