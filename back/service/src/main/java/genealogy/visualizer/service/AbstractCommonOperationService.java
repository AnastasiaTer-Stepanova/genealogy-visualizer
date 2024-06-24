package genealogy.visualizer.service;

import genealogy.visualizer.dto.EntityFilter;
import genealogy.visualizer.mapper.CommonMapper;
import genealogy.visualizer.mapper.EasyCommonMapper;
import genealogy.visualizer.mapper.FilterMapper;
import genealogy.visualizer.model.exception.BadRequestException;
import genealogy.visualizer.model.exception.NotFoundException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.List;

public class AbstractCommonOperationService<E, D, F, ED, FE extends EntityFilter> implements CrudService<D>, FilterService<ED, F> {

    private final CrudDAO<E> crudDAO;
    private final FilterDAO<E, FE> filterDAO;
    private final CommonMapper<D, E> mapper;
    private final FilterMapper<FE, F> filterMapper;
    private final EasyCommonMapper<ED, E> easyDataMapper;

    public AbstractCommonOperationService(CrudDAO<E> crudDAO, FilterDAO<E, FE> filterDAO, CommonMapper<D, E> mapper, FilterMapper<FE, F> filterMapper, EasyCommonMapper<ED, E> easyDataMapper) {
        this.crudDAO = crudDAO;
        this.filterDAO = filterDAO;
        this.mapper = mapper;
        this.filterMapper = filterMapper;
        this.easyDataMapper = easyDataMapper;
    }

    @Override
    public void delete(Long id) {
        try {
            crudDAO.delete(id);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public D getById(Long id) {
        try {
            return mapper.toDTO(crudDAO.findFullInfoById(id));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public D save(D data) {
        if (data == null) {
            throw new BadRequestException("Data is null");
        }
        try {
            return mapper.toDTO(crudDAO.save(mapper.toEntity(data)));
        } catch (IllegalArgumentException | EmptyResultDataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public D update(D data) {
        if (data == null) {
            throw new BadRequestException("Data is null");
        }
        try {
            return mapper.toDTO(crudDAO.update(mapper.toEntity(data)));
        } catch (IllegalArgumentException | EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    @Override
    public List<ED> filter(F filter) {
        try {
            return easyDataMapper.toDTOs(filterDAO.filter(filterMapper.toFilter(filter)));
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
