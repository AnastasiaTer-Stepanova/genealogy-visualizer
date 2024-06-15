package genealogy.visualizer.service;

import java.util.List;

public interface ParamDAO {

    Boolean getBooleanParamOrDefault(String paramName, Boolean defaultValue);

    List<String> getListStringOrDefault(String paramName, List<String> defaultValue);

    void updateValueByName(String name, String newValue);

}
