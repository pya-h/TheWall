package wallclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class AuthenticateApp extends ClientApp {

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
        return response.replace("\n", ""); // token + \n
    }


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
        return response.replace("\n", "");
    }

    public static void menu() throws IOException, UnsupportedEncodingException, HttpExceptions {
        while (loginToken == null || loginToken.equals("")) {
            clearScreen();
            System.out.println("--------------------- Login Menu ------------------------------------------\n");
            System.out.println("\t\t\t1. Login\n\t\t\t2. Register");
            System.out.print("GO: ");
            String destination = scanner.next();
            switch (destination) {
                case "1":
                    loginToken = doLogin(scanner);
                    System.out.println(loginToken);
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

    }
}
