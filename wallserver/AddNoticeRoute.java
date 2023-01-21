package wallserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

public class AddNoticeRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        try {

            final String token = this.parameters.get("token").toString();
            Account owner = Account.getAccountByToken(token);
            final String title = this.parameters.get("title").toString(),
                    description = this.parameters.get("description").toString(),
                    location = this.parameters.get("location").toString(),
                    phoneNumber = this.parameters.get("phoneNumber").toString(),
                    email = this.parameters.get("email").toString(),
                    images = this.parameters.get("images").toString();

            final long price = Long.parseLong(this.parameters.get("price").toString());

            Notice notice = Notice.add(owner, title, price, description, location, phoneNumber, email, images);
            System.out.println(owner.getUsername() + " added new notice with id:" + notice.getID() + " !");
            this.sendResponse(HttpURLConnection.HTTP_OK, String.valueOf(notice.getID()));
        }

        catch(WrongTokenException wtx) {
            // TODO: send proper message to client
            System.out.println(wtx.getMessage());
            sendResponse(HttpURLConnection.HTTP_FORBIDDEN);
        } catch (BadInputException | NumberFormatException ex) {
            System.out.println("Wrong Notice Data: Provided data doesn't met the standard patterns!");
            sendResponse(HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
        catch(IOException iox) {
            // Todo: send response
            System.out.println(iox.getMessage());
        }
    }
    
}