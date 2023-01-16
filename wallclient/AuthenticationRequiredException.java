package wallclient;

public class AuthenticationRequiredException extends Exception {
    public AuthenticationRequiredException() {
        super("You need to login/register first! Rerouting to Login Menu...");
    }
}