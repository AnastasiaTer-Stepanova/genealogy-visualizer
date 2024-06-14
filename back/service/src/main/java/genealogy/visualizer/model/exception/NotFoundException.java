package genealogy.visualizer.model.exception;

public class NotFoundException extends RuntimeException {

    public static final String MESSAGE = "Данные не найдены";

    public NotFoundException() {
        super(MESSAGE);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
