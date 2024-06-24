package genealogy.visualizer.mapper;

public interface CommonMapper<D, E> {

    E toEntity(D dto);

    D toDTO(E entity);
}
