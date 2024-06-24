package genealogy.visualizer.service.marriage;

import genealogy.visualizer.api.model.EasyMarriage;
import genealogy.visualizer.api.model.Marriage;
import genealogy.visualizer.api.model.MarriageFilter;
import genealogy.visualizer.dto.MarriageFilterDTO;
import genealogy.visualizer.mapper.EasyMarriageMapper;
import genealogy.visualizer.mapper.MarriageMapper;
import genealogy.visualizer.service.AbstractCommonOperationService;
import genealogy.visualizer.service.MarriageDAO;

import java.util.List;

public class MarriageServiceImpl extends AbstractCommonOperationService<genealogy.visualizer.entity.Marriage, Marriage, MarriageFilter, EasyMarriage, MarriageFilterDTO>
        implements MarriageService {

    public MarriageServiceImpl(MarriageDAO marriageDAO,
                               MarriageMapper marriageMapper,
                               EasyMarriageMapper easyMarriageMapper) {
        super(marriageDAO, marriageDAO, marriageMapper, marriageMapper, easyMarriageMapper);
    }

    @Override
    public void delete(Long id) {
        super.delete(id);
    }

    @Override
    public Marriage getById(Long id) {
        return super.getById(id);
    }

    @Override
    public Marriage save(Marriage marriage) {
        return super.save(marriage);
    }

    @Override
    public Marriage update(Marriage marriage) {
        return super.update(marriage);
    }

    @Override
    public List<EasyMarriage> filter(MarriageFilter filter) {
        return super.filter(filter);
    }
}
