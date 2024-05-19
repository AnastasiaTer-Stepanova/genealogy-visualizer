package genealogy.visualizer.service;

import genealogy.visualizer.entity.Death;

public interface DeathDAO {
    Death save(Death death);

    void updatePersonIdByPersonId(Long personId, Long newPersonId);

    void updatePersonIdById(Long id, Long newPersonId);

}
