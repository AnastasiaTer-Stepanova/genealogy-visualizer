package genealogy.visualizer.service.util;

import genealogy.visualizer.api.model.ErrorResponse;
import genealogy.visualizer.service.util.record.ResponseRecord;

public class ErrorHelper<T> {
    public ResponseRecord<T> NOT_FOUND_ERROR = new ResponseRecord<>(new ErrorResponse(404, "Данные не найдены"), null);
    public ResponseRecord<T> BAD_REQUEST_ERROR = new ResponseRecord<>(new ErrorResponse(400, "Переданы некорректные данные"), null);
}
