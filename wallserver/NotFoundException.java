package wallserver;

public class NotFoundException extends Exception {
    public NotFoundException(String url) {
        super(String.format("The url:%s NOT FOUND!", url));
    }
}
