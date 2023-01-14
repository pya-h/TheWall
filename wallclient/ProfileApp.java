package wallclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProfileApp extends ClientApp {

    public static String update(String field) throws HttpExceptions, IOException {
        HashMap<String, String> data = new HashMap<>();
        System.out.printf("New %s: ", field);
        String newValue = scanner.nextLine();
        data.put("token", loginToken);
        data.put("field", field);
        data.put("value", newValue);
        return post("profile/edit", data);
    }
    public static String update(String field, String password) throws HttpExceptions, IOException {
        HashMap<String, String> data = new HashMap<>();
        System.out.printf("New %s: ", field);
        String newValue = scanner.nextLine();
        data.put("token", loginToken);
        data.put("password", password);
        data.put("field", field);
        data.put("value", newValue);
        return post("profile/edit", data);
    }
    public static void menu() throws AuthenticationRequiredException, HttpExceptions, IOException {
        try {
            HashMap<String, String> data = new HashMap<>();
            data.put("token", loginToken);
            String profile = post("profile/view", data);
            while(true) {
                try {
                    clearScreen();
                    System.out.println("--------------------- Your Profile ------------------------------------------\n");

                    String profileInAlignedText = String.format("\n\t\t\t%s", profile.replaceAll("\n", "\n\t\t\t"));
                    System.out.println(profileInAlignedText); // just making the text in cli aligned with others
                    pause();
                    System.out.println("------------------------- Edit --------------------------------------\n");
                    System.out.println("\t\t\t1. Change Username\n\t\t\t2. Change First Name\n\t\t\t3. Change Last Name\n\t\t\t4. Add Avatar\n\t\t\t5. Change Password");
                    String choice = scanner.next();
                    scanner = new Scanner(System.in); // prevent bug that will be cause when using .next, .nextLine, .nextInt abd .,..
                    switch(choice) {
                        case "1":
                            profile = update("Username");
                            break;
                        case "2":
                            profile = update("FirstName");
                            break;
                        case "3":
                            profile = update("LastName");
                            break;
                        case "4":
                            profile = update("Avatar");
                            break;
                        case "5":
                            System.out.print("Enter your current password: ");
                            String currentPassword = scanner.nextLine();
                            profile = update("Password", currentPassword);
                            break;
                        default:
                            System.out.println("Wrong choice! There is no such field to change!...");
                    }
                }
                catch(UnauthorizedException ux) {
                    System.out.println("In order to chnage this field, you need to enter your current password correct!");
                }
                pause();
            }

        }
        catch(ForbiddenException fx) {
            System.out.println("This action needs you to be logged into your account!");
                throw new AuthenticationRequiredException();
        }
    }
}
