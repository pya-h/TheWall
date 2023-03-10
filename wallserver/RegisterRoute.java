package wallserver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.regex.Pattern;

import com.sun.net.httpserver.HttpExchange;

public class RegisterRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        try {
            String username = this.parameters.get("username").toString(),
            password = this.parameters.get("password").toString();

            System.out.println(username + " " + password);
            Account newUser = Account.register(username, password);
            String response = newUser.getLoginToken();
            System.out.println("New user: " + username + " has registered!");
            this.sendResponse(HttpURLConnection.HTTP_OK, response);
        }
        catch(UnsupportedEncodingException uex) {
            // Todo: send proper response to client
            System.out.println("Something went wrong while processing request because: " + uex.getMessage());

        }
        catch(UsernameExistsException uex) {
            System.out.println(uex.getMessage());
            sendResponse(HttpURLConnection.HTTP_CONFLICT);
        } catch (BadInputException bix) {
            System.out.println(bix);
            sendResponse(HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
        catch(IOException iox) {
            System.out.println(iox.getMessage());
            sendResponse(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }
    
}