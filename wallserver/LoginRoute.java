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
            System.out.println(cde.getMessage());
            this.sendResponse(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
        catch(WrongCredentialsException ex) {
            System.out.println(ex.getMessage());
            this.sendResponse(HttpURLConnection.HTTP_UNAUTHORIZED);

        } catch (BadInputException ignored) { }
        catch(IOException iox) {
            System.out.println(iox.getMessage());
            sendResponse(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

}
