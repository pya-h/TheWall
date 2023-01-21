package wallserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class GetNoticesRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        try {
            String token = this.parameters.get("token").toString();
            String location = this.parameters.containsKey("location") ? this.parameters.get("location").toString() : null,
                search = this.parameters.containsKey("search") ? this.parameters.get("search").toString() : null,
                sort = this.parameters.containsKey("sort") ? this.parameters.get("sort").toString() : null,
                section = this.parameters.containsKey("section") ? this.parameters.get("section").toString() : null;

            long min = this.parameters.containsKey("min") ? Long.parseLong(this.parameters.get("min").toString()) : -1,
                max = this.parameters.containsKey("max") ? Long.parseLong(this.parameters.get("max").toString()) : -1;

            Account account = Account.getAccountByToken(token);
            ArrayList<Notice> notices = new ArrayList<>();
            if(section != null && section.toLowerCase().equals("fav")) {
                notices = account.getFavorites();
                System.out.println(account.getUsername() + " has requested to view his/her favorite list!");
            }
            else if(location != null) {
                notices = Notice.getNotices(location);
                System.out.println(account.getUsername() + " has requested to view notices in " + location + "!");
            }
            else if(search != null) {
                notices = Notice.search(search);
                System.out.println(account.getUsername() + " has requested to search notices for " + search + "!");
            }

            if(min >= 0)
                notices.removeIf(n -> n.getPrice() < min);

            if(max >= 0)
                notices.removeIf(n -> n.getPrice() > max);

            if(sort != null) {
                switch(sort) {
                    case "+p":
                        notices.sort((n1, n2) -> Long.compare(n1.getPrice(), n2.getPrice()));
                        break;
                    case "-p":
                        notices.sort((n1, n2) -> Long.compare(n2.getPrice(), n1.getPrice()));
                        break;

                    case "+l":
                        notices.sort((n1, n2) -> Long.compare(n1.getLastChangeDate().getTime(),
                                n2.getLastChangeDate().getTime()));
                        break;
                    case "-l":
                        notices.sort((n1, n2) -> Long.compare(n2.getLastChangeDate().getTime(),
                                n1.getLastChangeDate().getTime()));
                        break;

                    default:
                        break;
                }
            }

            StringBuilder res = new StringBuilder();
            for(Notice n: notices)
                res.append(n.toString()).append("\n");
            final String response = res.toString();
            this.sendResponse(HttpURLConnection.HTTP_OK, response != null && !response.equals("") ? response : "-");
        }
        catch(WrongTokenException wtx) {
            System.out.println(wtx.getMessage());
            sendResponse(HttpURLConnection.HTTP_FORBIDDEN);
        }
        catch(IOException iox) {
            System.out.println(iox.getMessage());
            sendResponse(HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }

}