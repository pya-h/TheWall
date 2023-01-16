package wallserver;

public class UsernameExistsException extends Exception {
    public UsernameExistsException(String username) {
        super(String.format("User with username:%s already exists!", username));
    }
}
