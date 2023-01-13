package wallserver;

public class WrongTokenException extends Exception {
    public WrongTokenException(String token) {
        super(String.format("No user is logged in associated with '%s' token! The requesting user needs to reauthenticate...", token));
    }
}