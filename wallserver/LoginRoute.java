package wallserver;
import java.io.IOException;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

public class LoginRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        try {
            String username = this.parameters.get("username").toString(),
            password = this.parameters.get("password").toString();
            Account user = Account.login(username, password);
            String response = user.getLoginToken();
            System.out.println(username + " has logged in!");
            this.sendResponse(HttpURLConnection.HTTP_OK, response);
        }
        catch(CorruptedDataException cde) {
            // TODO: send proper message to client
            System.out.println(cde.getMessage());
        }
        catch(WrongCredentialsException ex) {
            // TODO: send proper message to client
            System.out.println(ex.getMessage());
            this.sendResponse(HttpURLConnection.HTTP_UNAUTHORIZED, ex.getMessage());

        }
        catch(IOException iox) {
            // Todo: send response
            System.out.println(iox.getMessage());
        }
    }
    
}