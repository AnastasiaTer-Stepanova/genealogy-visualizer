package genealogy.visualizer.service;

import genealogy.visualizer.dto.DeathFilterDTO;
import genealogy.visualizer.entity.Death;

import java.util.List;

public interface DeathDAO {

    void delete(Long id);

    Death save(Death death);

    Death update(Death death);

    Death findFullInfoById(Long id);

    List<Death> filter(DeathFilterDTO filter);
}
