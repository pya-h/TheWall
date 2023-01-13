package wallserver;
import java.io.IOException;
import java.io.OutputStream;

import com.sun.net.httpserver.*;

public class RootRoute implements HttpHandler {
    @Override public void handle(HttpExchange httpExchange) throws IOException {
        String res = "Welcome!";
        httpExchange.sendResponseHeaders(200, res.length());
        OutputStream bodyStream = httpExchange.getResponseBody();
        bodyStream.write(res.getBytes());
        bodyStream.close();
    } 
}
