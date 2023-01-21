package wallserver;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class GetLocationsRoute implements HttpHandler {
    @Override public void handle(HttpExchange httpExchange) throws IOException {
        ArrayList<String> locations = Notice.getLocations();
        StringBuilder res = new StringBuilder();
        for(String loc: locations)
            res.append(loc + "\n");
        final String response = res.toString();
        httpExchange.sendResponseHeaders(200, response.length());
        OutputStream bodyStream = httpExchange.getResponseBody();
        bodyStream.write(response.getBytes());
        bodyStream.close();
    }
}
