package genealogy.visualizer.service.impl;

import genealogy.visualizer.repository.ParamRepository;
import genealogy.visualizer.service.ParamDAO;

public class ParamDAOImpl implements ParamDAO {

    private final ParamRepository paramRepository;

    public ParamDAOImpl(ParamRepository paramRepository) {
        this.paramRepository = paramRepository;
    }

    @Override
    public Boolean getBooleanParamOrDefault(String paramName, Boolean defaultValue) {
        return paramRepository.findByName(paramName)
                .map(param -> Boolean.parseBoolean(param.getValue()))
                .orElse(defaultValue);
    }
}