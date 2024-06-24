package genealogy.visualizer.service;

public interface CrudService<D> {

    void delete(Long id);

    D getById(Long id);

    D save(D data);

    D update(D data);
}
