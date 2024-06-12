package genealogy.visualizer.service;

import genealogy.visualizer.dto.LocalityFilterDTO;
import genealogy.visualizer.entity.Locality;

import java.util.List;

public interface LocalityDAO {

    void delete(Long id);

    Locality save(Locality locality);

    Locality update(Locality locality);

    Locality findFullInfoById(Long id);

    List<Locality> filter(LocalityFilterDTO filter);

    Locality saveOrFindIfExist(Locality archiveDocument);

}
