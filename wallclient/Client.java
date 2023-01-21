package wallclient;

import wallserver.WrongTokenException;

import java.io.IOException;
import java.util.HashMap;

public class Client extends ClientApp {
    public static void logout() throws IOException, HttpExceptions, WrongTokenException {
        try {

            HashMap<String, String> data = new HashMap<>();
            data.put("token", loginToken);
            String result = post("logout", data);
            System.out.println(result);
            loginToken = null;
        }
        catch (ForbiddenException fx) { throw new WrongTokenException(loginToken); }
    }
    public static void main(String[] args) {
        while(true) {

            try {
                String res = get("");
                // Runtime.getRuntime().exec("clear");
                System.out.println(res);

                AuthenticateApp.menu();
                // now the user is logged in:
                clearScreen();
                String destination = "";
                while (!destination.equals("0")) {
                    System.out.println("--------------------- Main Menu ------------------------------------------\n");
                    System.out.println("\t\t\t1. Profile\n\t\t\t2. Notices\n\t\t\t0. Log out");
                    System.out.print("GO: ");
                    destination = scanner.next();
                    switch (destination) {
                        case "1":
                            ProfileApp.menu();
                            break;
                        case "2":
                            // show an d check notices
                            NoticeApp.menu();
                            break;
                        case "0":
                            // TODO: send logout request to server to remove token from list
                            logout();
                            pause();
                            break;
                        default:
                            System.out.println("Wrong destination! Please try again...");
                    }
                }

            }
            catch(AuthenticationRequiredException arx) {
                System.out.println(arx.getMessage());
                loginToken = null;
                pause();
            }
            catch(WrongTokenException wtx) {
                System.out.println(wtx.getMessage() +
                        "\n\tActually it seems, this user hasn't logged in, in the first place or maybe his/her session has passed!");
                loginToken = null;
                pause();
            }
            catch (Exception ex) {
                System.out.println("Something unexpected has happened, cause: " + ex.getMessage());
                System.out.println("Note: Make sure server application is up and running !");
                break;
            }

        }
        scanner.close();
        try {

            if(loginToken != null && !loginToken.equals(""))
                logout();
        }
        catch (Exception ignored) {}
    }
}
