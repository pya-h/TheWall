package wallserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Locale;

public class EditProfileRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        Account account = null;
        try {
            String token = this.parameters.get("token").toString(),
                    field = this.parameters.get("field").toString().toLowerCase(),
                    value = this.parameters.get("value").toString();
            account = Account.getAccountByToken(token);
            switch (field) {
                case "username" -> account.setUsername(value);
                case "firstname" -> account.setFirstName(value);
                case "lastname" -> account.setLastName(value);
                case "password" -> {
                    String password = this.parameters.get("password").toString();
                    if (!account.getPassword().equals(password))
                        throw new WrongCredentialsException();
                    account.setPassword(value);
                }
                case "avatar" ->
                    // TODO: check image exists on the net	h
                    account.addAvatar(value);
                default -> throw new BadRequestException("User attempts to change a profile field that doesnt exists!");
            }
            account.save();
            System.out.printf("%s changed his/her %s successfully!\n", account.getUsername(), field);
            this.sendResponse(HttpURLConnection.HTTP_OK, account.toString());
        }
        catch(WrongTokenException wtx) {
            // TODO: send proper message to client
            System.out.println(wtx.getMessage());
            sendResponse(HttpURLConnection.HTTP_FORBIDDEN);
        }
        catch (WrongCredentialsException wcx) {
            System.out.printf("User:%s attempted to password change without entering the correct current password!%n", account != null ? account.getUsername() : "{???}");
            sendResponse(HttpURLConnection.HTTP_UNAUTHORIZED);
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            sendResponse(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        catch(IOException iox) {
            // Todo: send response
            System.out.println(iox.getMessage());
        }
    }
    
}