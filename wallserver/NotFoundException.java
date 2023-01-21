package wallserver;

public class NotFoundException extends Exception {
    public NotFoundException(String urlOrID) {
        super(String.format("Item:%s NOT FOUND!", urlOrID));
    }
}
