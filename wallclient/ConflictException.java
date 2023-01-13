package wallclient;

public class ConflictException extends HttpExceptions {
    public ConflictException() {
        super("409: Conflict!");
    }
}