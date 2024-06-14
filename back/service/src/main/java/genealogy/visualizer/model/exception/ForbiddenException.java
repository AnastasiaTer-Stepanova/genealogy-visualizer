package genealogy.visualizer.model.exception;

public class ForbiddenException extends RuntimeException {

    public static final String MESSAGE = "Действие запрещено";

    public ForbiddenException() {
        super(MESSAGE);
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
