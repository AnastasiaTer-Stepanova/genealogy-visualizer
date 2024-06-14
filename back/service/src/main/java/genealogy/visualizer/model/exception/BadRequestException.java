package genealogy.visualizer.model.exception;

public class BadRequestException extends RuntimeException {

    public static final String MESSAGE = "Переданы некорректные данные";

    public BadRequestException() {
        super(MESSAGE);
    }

    public BadRequestException(String message) {
        super(message);
    }
}
