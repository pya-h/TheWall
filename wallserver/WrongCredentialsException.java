package wallserver;

public class WrongCredentialsException extends Exception {
    public WrongCredentialsException() {
        super("Username or password is not correct!");
    }
}
