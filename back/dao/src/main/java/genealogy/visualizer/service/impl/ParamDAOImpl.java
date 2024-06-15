package genealogy.visualizer.service.impl;

import genealogy.visualizer.repository.ParamRepository;
import genealogy.visualizer.service.ParamDAO;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class ParamDAOImpl implements ParamDAO {

    private static final String COMMA = ", ";

    private final ParamRepository paramRepository;

    public ParamDAOImpl(ParamRepository paramRepository) {
        this.paramRepository = paramRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean getBooleanParamOrDefault(String paramName, Boolean defaultValue) {
        return paramRepository.findByName(paramName)
                .map(param -> Boolean.parseBoolean(param.getValue()))
                .orElse(defaultValue);
    }

    @Override
    public List<String> getListStringOrDefault(String paramName, List<String> defaultValue) {
        return paramRepository.findByName(paramName)
                .map(param -> List.of(param.getValue().split(COMMA)))
                .orElse(defaultValue);
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateValueByName(String name, String newValue) {
        paramRepository.updateValueByName(name, newValue);
    }
}