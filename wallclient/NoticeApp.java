package wallclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class NoticeApp extends ClientApp {
    public static long newNotice() throws HttpExceptions, IOException {
        String response = "";

        try {
            System.out.println("Now enter your notice info in the next section...");
            pause();
            clearScreen();

            String title, description, location, phoneNumber, email;
            long price;
            System.out.println("------------------------- New Notice --------------------------------\n");
            System.out.print("Title: ");
            title = scanner.nextLine();
            while(true) {
                try {
                    System.out.print("Price: ");
                    String strPrice = scanner.nextLine();
                    price = Long.parseLong(strPrice);
                    break;
                }
                catch(NumberFormatException nfx) {
                    System.out.println("please enter a valid price value!");
                }
            }
            System.out.print("Description: ");
            description = scanner.nextLine();
            System.out.print("Location: ");
            location = scanner.nextLine();
            System.out.print("Phone Number: ");
            phoneNumber = scanner.nextLine();

            while(!Pattern.matches("[+]?\\d+", phoneNumber)) {
                System.out.println("Please enter a valid phone number!");
                System.out.print("Phone Number: ");
                phoneNumber = scanner.nextLine();
            }

            System.out.print("Email: ");
            email = scanner.nextLine();
            while(!Pattern.matches("[a-zA-Z0-9.]*[@]{1}.[a-zA-Z0-9.]*", email)) {
                System.out.println("Please enter a valid email!");
                System.out.print("Email: ");
                email = scanner.nextLine();
            }
            HashMap<String, String> data = new HashMap<>();
            data.put("token", loginToken);
            data.put("title", title);
            data.put("price", String.valueOf(price));
            data.put("description", description);
            data.put("location", location);
            data.put("phoneNumber", phoneNumber);
            data.put("email", email);

            response = post("notices/+", data);
            // TODO: check for exception
        } catch (UnauthorizedException ex) {
            System.out.println("ERROR: Username or password is incorrect!");
        }
        return Long.parseLong(response.replace("\n", ""));
    }

    public static void menu() throws AuthenticationRequiredException, HttpExceptions, IOException {
        while(true) {
            try {
                clearScreen();
                System.out.println("------------------------- Notices -----------------------------------\n");
                System.out.println("\t\t\t1. Add New Notice\n\t\t\t2. View Notices");
                String choice = scanner.next();
                scanner = new Scanner(System.in); // prevent bug that will be cause when using .next, .nextLine, .nextInt abd .,..
                switch(choice) {
                    case "1":
                        long noticeID = newNotice();
                        System.out.println("Notice: " + String.valueOf(noticeID) + " successfully added!");
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
            catch(ForbiddenException fx) {
                System.out.println("This action needs you to be logged into your account!");
                    throw new AuthenticationRequiredException();
            }
            catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
            pause();
        }
    }
}
