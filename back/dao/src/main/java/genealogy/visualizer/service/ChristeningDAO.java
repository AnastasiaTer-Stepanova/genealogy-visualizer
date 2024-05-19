package genealogy.visualizer.service;

import genealogy.visualizer.entity.Christening;

public interface ChristeningDAO {

    Christening save(Christening christening);

    void updatePersonIdByPersonId(Long personId, Long newPersonId);

    void updatePersonIdById(Long id, Long newPersonId);
}
