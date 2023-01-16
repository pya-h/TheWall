package wallserver;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sun.net.httpserver.*;
public class PostRequestHandler implements HttpHandler {
    protected Map<String, Object> parameters;
    protected HttpExchange httpExchange;
    public static final int BUFFER_SIZE = 1024;
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        this.httpExchange = httpExchange;
        this.parameters = extractBody(httpExchange.getRequestBody());
    }

    public static Map<String, Object> extractBody(InputStream body)
        throws UnsupportedEncodingException, IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(body, "utf-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String query = bufferedReader.readLine();
        Map<String, Object> parameters = new HashMap<String, Object>();
        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                    System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                    System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);
                    }
                    else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                }
                else {
                    parameters.put(key, value);
                }
            }
        }
        return parameters;
    }

    public void sendResponse(int responseCode) throws IOException {
        this.httpExchange.sendResponseHeaders(responseCode,0);
    }
    public void sendShortResponse(int responseCode, String response) throws IOException {
        this.httpExchange.sendResponseHeaders(responseCode, response.length());
        OutputStream bodyStream = this.httpExchange.getResponseBody();
        bodyStream.write(response.getBytes());
        bodyStream.close();
    }
    public void sendResponse(int responseCode, String response) throws IOException {
        // this algorithm sends responses with any length without throwing exception for large messages
        httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpExchange.getResponseBody())) {
            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8))) {
                byte [] buffer = new byte [BUFFER_SIZE];
                int segmentLength;
                while ((segmentLength = byteArrayInputStream.read(buffer)) != -1) {
                    bufferedOutputStream.write(buffer, 0, segmentLength);
                }
                bufferedOutputStream.close();
            }
        }
    }
}
