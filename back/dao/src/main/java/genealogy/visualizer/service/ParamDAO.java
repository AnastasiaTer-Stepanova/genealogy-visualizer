package genealogy.visualizer.service;

public interface ParamDAO {

    Boolean getBooleanParamOrDefault(String paramName, Boolean defaultValue);

    void updateValueByName(String name, String newValue);

}
