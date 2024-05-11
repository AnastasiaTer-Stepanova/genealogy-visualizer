package genealogy.visualizer.service.util;

import genealogy.visualizer.api.model.Error;

public class ErrorHelper {
    public static final Error NOT_FOUND_ERROR = new Error(404, "Данные не найдены");
    public static final Error BAD_REQUEST_ERROR = new Error(400, "Переданы некорректные данные");
}
