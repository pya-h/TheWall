package wallserver;

public class MethodNotAllowedException extends Exception {
    public MethodNotAllowedException(String message) {
        super("Cannot compete the request because: " + message);
    }
}
