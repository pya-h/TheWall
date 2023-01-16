package wallserver;
import java.io.IOException;
import java.net.HttpURLConnection;

import com.sun.net.httpserver.HttpExchange;

public class ViewProfileRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        try {
            String token = this.parameters.get("token").toString();
            Account account = Account.getAccountByToken(token);
            System.out.println(account.getUsername() + " has requested to view his/her profile!");
            this.sendResponse(HttpURLConnection.HTTP_OK, account.toString());
        }
        catch(WrongTokenException wtx) {
            // TODO: send proper message to client
            System.out.println(wtx.getMessage());
            sendResponse(HttpURLConnection.HTTP_FORBIDDEN);
        }
        catch(IOException iox) {
            // Todo: send response
            System.out.println(iox.getMessage());
        }
    }
    
}