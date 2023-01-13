package wallclient;

public class ForbiddenException extends HttpExceptions {
    public ForbiddenException() {
        super("403: Forbidden!");
    }
}