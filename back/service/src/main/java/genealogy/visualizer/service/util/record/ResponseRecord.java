package genealogy.visualizer.service.util.record;

import genealogy.visualizer.api.model.ErrorResponse;

public record ResponseRecord<T>(ErrorResponse error, T value) {
}
