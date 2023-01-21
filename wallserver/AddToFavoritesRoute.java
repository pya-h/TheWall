package wallserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.HttpURLConnection;

public class AddToFavoritesRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        try {
            String token = this.parameters.get("token").toString();
            long id = Long.parseLong(this.parameters.get("id").toString());
            Account account = Account.getAccountByToken(token);
            System.out.printf("%s has requested to add notice:%d to his/her favorites list!\n", account.getUsername(), id);
            boolean added = account.addToFavorites(id);
            this.sendResponse(HttpURLConnection.HTTP_OK, added ? "Successfully added to your favorite list!"
                    : "Successfully removed from your favourite list!");
        }
        catch(NumberFormatException nfx) {
            System.out.printf("Cannot add notice to %s favorites cause: \n", nfx.getMessage());
            sendResponse(HttpURLConnection.HTTP_BAD_REQUEST);
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
