package genealogy.visualizer.service;

import genealogy.visualizer.dto.MarriageFilterDTO;
import genealogy.visualizer.entity.Marriage;

import java.util.List;

public interface MarriageDAO {

    void delete(Long id);

    Marriage save(Marriage marriage);

    Marriage update(Marriage marriage);

    Marriage findFullInfoById(Long id);

    List<Marriage> filter(MarriageFilterDTO filter);

}
