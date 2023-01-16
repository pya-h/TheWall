package wallclient;

public class NotFoundException extends HttpExceptions {
    public NotFoundException() {
        super("404: Not Found!");
    }
}