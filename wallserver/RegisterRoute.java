package wallserver;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.tools.jconsole.JConsoleContext;

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
            this.sendResponse(200, response);
        }
        catch(UnsupportedEncodingException uex) {
            // Todo: send proper response to client
            System.out.println("Something went wrong while processing request because: " + uex.getMessage());

        }
        catch(UsernameExistsException uex) {
            // TODO: send proper message to client
            System.out.println(uex.getMessage());
        }
        catch(IOException iox) {
            System.out.println(iox.getMessage());
        }
    }
    
}