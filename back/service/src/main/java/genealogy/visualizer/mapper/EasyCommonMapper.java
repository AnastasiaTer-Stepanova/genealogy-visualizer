package genealogy.visualizer.mapper;

import java.util.List;

public interface EasyCommonMapper<ED, E> {

    List<ED> toDTOs(List<E> entities);

}
