package wallserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

public class LogoutRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        try {
            String token = this.parameters.get("token").toString();

            Account account = Account.getAccountByToken(token);
            account.logout();
            System.out.printf("%s logged out!\n", account.getUsername());
            sendResponse(HttpURLConnection.HTTP_OK, "Successfully Logged out!");
        }
        catch(WrongTokenException wtx) {
            System.out.println(wtx.getMessage());
            sendResponse(HttpURLConnection.HTTP_FORBIDDEN);
        }

    }
    
}