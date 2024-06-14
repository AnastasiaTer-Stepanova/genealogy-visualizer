package genealogy.visualizer.model.exception;

public class UnauthorizedException extends RuntimeException {

    public static final String MESSAGE = "Доступ запрещен. Пожалуйста, авторизуйтесь.";

    public UnauthorizedException() {
        super(MESSAGE);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
