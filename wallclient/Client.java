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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringJoiner;

public class Client {
    public static final String ROOT = "http://localhost:7001";
    public static final String USER_AGENT = "Mozilla/5.0";

    public static String getResponse(HttpURLConnection connection) throws HttpExceptions, IOException {
        StringBuffer res = new StringBuffer();

        int resCode = connection.getResponseCode();
        if (resCode != HttpURLConnection.HTTP_OK) {
            if (resCode == HttpURLConnection.HTTP_UNAUTHORIZED)
                throw new UnauthorizedException();
            else if (resCode == HttpURLConnection.HTTP_CONFLICT)
                throw new ConflictException();
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

    public static String makeRegistration(Scanner scanner)
            throws UnsupportedEncodingException, IOException, HttpExceptions {
        String response = "";

        try {
            String username, password;
            System.out.print("Username: ");
            username = scanner.next();
            System.out.print("Password: ");
            password = scanner.next();
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            response = post("register", data);
            // TODO: check for exception
        } catch (ConflictException cex) {
            System.out.println("This username is already taken!");
        }
        return response;
    }

    private static String loginToken;

    public static String doLogin(Scanner scanner) throws HttpExceptions, IOException {
        String response = "";

        try {
            String username, password;
            System.out.print("Username: ");
            username = scanner.next();
            System.out.print("Password: ");
            password = scanner.next();
            Map<String, String> data = new HashMap<>();
            data.put("username", username);
            data.put("password", password);
            response = post("login", data);
            // TODO: check for exception
        } catch (UnauthorizedException ex) {
            System.out.println("ERROR: Username or password is incorrect!");
        }
        return response;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            String res = get("");
            // Runtime.getRuntime().exec("clear");
            System.out.println(res);

            while (loginToken == null || loginToken.equals("")) {
                clearScreen();
                System.out.println("--------------------- Login Menu ------------------------------------------\n");
                System.out.println("\t\t\t1. Login\n\t\t\t2. Register");
                System.out.print("GO: ");
                String destination = scanner.next();
                switch (destination) {
                    case "1":
                        loginToken = doLogin(scanner);
                        break;
                    // TODO: login code
                    case "2":
                        loginToken = makeRegistration(scanner);
                        break;
                    default:
                        System.out.println("Wrong destination! Please try again...");
                }
                pause();
            }
            // now the user is logged in:
            clearScreen();
            while (true) {
                System.out.println("--------------------- Main Menu ------------------------------------------\n");
                System.out.println("t\t\t1. Profile\n\t\t\t2. Notices");
                System.out.print("GO: ");
                String destination = scanner.next();
                switch (destination) {
                    case "1":
                        showProfile(scanner);
                        break;
                    case "2":
                        // show an d check notices
                        break;
                    default:
                        System.out.println("Wrong destination! Please try again...");

                }
            }

        }

        catch (Exception ex) {
            // do sth
        }
        scanner.close();

    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
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
