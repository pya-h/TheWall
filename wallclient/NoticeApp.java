package wallclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

public class NoticeApp extends ClientApp {
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
                    System.out.println("\t\t\t1. Add New Notice\n\t\t\t");
                    String choice = scanner.next();
                    scanner = new Scanner(System.in); // prevent bug that will be cause when using .next, .nextLine, .nextInt abd .,..
                    switch(choice) {
                        case "1":
                            
                            break;
                        case "2":
                            break;
                        case "3":
                            break;
                        case "4":
                            break;

                        default:
                            System.out.println("Wrong destination! please try again...");
                    }
                }
                catch(Exception ex) {
                    System.out.println(ex.getMessage());
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
