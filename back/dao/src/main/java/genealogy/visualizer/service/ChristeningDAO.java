package genealogy.visualizer.service;

import genealogy.visualizer.dto.ChristeningFilterDTO;
import genealogy.visualizer.entity.Christening;

import java.util.List;

public interface ChristeningDAO {

    void delete(Long id);

    Christening save(Christening christening);

    Christening update(Christening christening);

    Christening findFullInfoById(Long id);

    List<Christening> filter(ChristeningFilterDTO filter);
}
