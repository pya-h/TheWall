package wallclient;

public class InternalServerError extends HttpExceptions {
    public InternalServerError() {
        super("500: Internal Server Error!");
    }
}