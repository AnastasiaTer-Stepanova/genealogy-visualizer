package genealogy.visualizer.mapper;

import genealogy.visualizer.dto.EntityFilter;

public interface FilterMapper<FE extends EntityFilter, F> {

    FE toFilter(F filter);
}
