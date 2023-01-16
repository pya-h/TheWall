package wallclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

public class ClientApp {
    public static final String ROOT = "http://localhost:7001";
    public static final String USER_AGENT = "Mozilla/5.0";
    protected static String loginToken;
    protected static Scanner scanner = new Scanner(System.in);

    public static String getResponse(HttpURLConnection connection) throws HttpExceptions, IOException {
        StringBuffer res = new StringBuffer();

        int resCode = connection.getResponseCode();
        if (resCode != HttpURLConnection.HTTP_OK) {
            if (resCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                throw new UnauthorizedException();
            else if (resCode == HttpURLConnection.HTTP_CONFLICT)
                throw new ConflictException();
            else if(resCode == HttpURLConnection.HTTP_FORBIDDEN)
                throw new ForbiddenException();
            else if(resCode == HttpURLConnection.HTTP_NOT_FOUND)
                throw new NotFoundException();
        }

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        for (; (line = inputStream.readLine()) != null; res.append(line + "\n"));
        inputStream.close();

        return res.toString();
    }

    public static String post(final String route, Map<String, String> body)
            throws UnsupportedEncodingException, IOException, HttpExceptions {
        // prepare body data
        StringJoiner joiner = new StringJoiner("&");
        for (Map.Entry<String, String> item : body.entrySet()) {
            joiner.add(URLEncoder.encode(item.getKey(), "UTF-8") + "="
                    + URLEncoder.encode(item.getValue(), "UTF-8"));
        }
        byte[] bodyAsBytes = joiner.toString().getBytes(StandardCharsets.UTF_8);
        // prepare connection
        URL url = new URL(ROOT + "/" + route);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setFixedLengthStreamingMode(bodyAsBytes.length);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        connection.connect();

        // post data
        try (OutputStream outputStream = connection.getOutputStream()) {
            outputStream.write(bodyAsBytes);
        }

        return getResponse(connection);
    }

    public static String get(final String route) throws IOException, HttpExceptions {
        URL url = new URL(ROOT + "/" + route);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        return getResponse(connection);
    }

    public static void clearScreen() {
        try {
            if(System.getProperty("os.name").startsWith("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
        }
        catch(Exception ex) {
            System.out.print("\033[H\033[2J");

        }
        System.out.flush();
    }

    public static void pause() {
        System.out.print("Press Enter key to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
        }
    }
}
