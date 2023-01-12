package wallclient;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;


public class Client {
    public static final String ROOT = "http://localhost:7001";
    public static final String USER_AGENT = "Mozilla/5.0";

    public static String getResponse(HttpURLConnection connection) throws HttpExceptions, IOException {
        StringBuffer res = new StringBuffer();

        int resCode = connection.getResponseCode();
        if (resCode != HttpURLConnection.HTTP_OK) {
            if (resCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                throw new UnauthorizedException();

        }

        BufferedReader inputStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        String line;
        for (; (line = inputStream.readLine()) != null; res.append(line + "\n"))
            ;
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

    public static String makeRegistration () {
        Scanner scanner = new Scanner(System.in);
        String response = "";

        try {
            String username, password;
            System.out.print("Username: ");
            username = scanner.nextLine();
            System.out.print("Password: ");
            password = scanner.nextLine();
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            response = post("register", data);
            // TODO: check for exception
            scanner.close();
        } catch (Exception ex) {
            System.out.println("Sth went wrong: " + ex.getMessage());
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return response;
    }

    private static String loginToken;

    public static String doLogin() throws HttpExceptions, IOException {
        Scanner scanner = new Scanner(System.in);
        String response = "";

        try {
            String username, password;
            System.out.print("Username: ");
            username = scanner.nextLine();
            System.out.print("Password: ");
            password = scanner.nextLine();
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            response = post("login", data);
            // TODO: check for exception
            scanner.close();
        } catch (UnauthorizedException ex) {
            System.out.println("ERROR: Username or password is incorrect!");
        } finally {
            if (scanner != null)
                scanner.close();
        }
        return response;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            // Runtime.getRuntime().exec("cls");
            while (loginToken == null || loginToken.equals("")) {
                String res = get("");
                System.out.println(res);
                System.out.println("Login Menu:\n\t1. Login\n\t2. Register");
                int destination = scanner.nextInt();
                switch (destination) {
                    case 1:
                        loginToken = doLogin();
                        break;
                    // TODO: login code
                    case 2:
                        loginToken = makeRegistration();
                        break;
                    default:
                        System.out.println("Wrong destination! Please try again...");
                }
                // Runtime.getRuntime().exec("pause");
            }

            // test
            System.out.println("Hi " + loginToken);
        }

        catch (Exception ex) {
            // do sth
        } finally {
            if (scanner != null)
                scanner.close();
        }
    }
}
