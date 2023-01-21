package wallclient;

public class NotAcceptableException extends HttpExceptions {
    public NotAcceptableException() {
        super("406: Not Acceptable!");
    }
}