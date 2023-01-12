package wallclient;

public class UnauthorizedException extends HttpExceptions {
    public UnauthorizedException() {
        super("401: Unauthorized!");
    }
}
