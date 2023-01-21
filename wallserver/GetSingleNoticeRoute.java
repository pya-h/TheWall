package wallserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class GetSingleNoticeRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        try {
            String token = this.parameters.get("token").toString();
            long id = Long.parseLong(this.parameters.get("id").toString());
            Account account = Account.getAccountByToken(token);
            System.out.println(account.getUsername() + " has requested to view notice: " + id + " ...");
            Notice notice = Notice.get(id);
            final String response = notice.toString(true);
            this.sendResponse(HttpURLConnection.HTTP_OK, response);
        }
        catch(WrongTokenException wtx) {
            System.out.println(wtx.getMessage());
            sendResponse(HttpURLConnection.HTTP_FORBIDDEN);
        }
        catch (NotFoundException e) {
            System.out.println("NoticeNotFound; " + e.getMessage());
            sendResponse(HttpURLConnection.HTTP_NOT_FOUND);
        }

        catch(IOException iox) {
            System.out.println(iox.getMessage());
            sendResponse(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

}