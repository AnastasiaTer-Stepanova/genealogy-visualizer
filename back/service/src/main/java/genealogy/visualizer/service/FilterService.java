package genealogy.visualizer.service;

import java.util.List;

public interface FilterService<ED, F> {

    List<ED> filter(F filter);

}
