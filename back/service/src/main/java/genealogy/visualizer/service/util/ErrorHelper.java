package genealogy.visualizer.service.util;

import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.service.util.record.ResponseRecord;

public class ErrorHelper<T> {
    public ResponseRecord<T> NOT_FOUND_ERROR = new ResponseRecord<>(new ErrorResponse("not_found", "Данные не найдены"), null);
}
