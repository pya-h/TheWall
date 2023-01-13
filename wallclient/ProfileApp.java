package wallclient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileApp extends ClientApp {

    public static void menu() throws AuthenticationRequiredException, HttpExceptions, IOException  {
        clearScreen();
        try {
            System.out.println("--------------------- Your Profile ------------------------------------------\n");
            Map<String, String> data = new HashMap<>();
            data.put("token", loginToken);
            final String profile = post("profile/view", data),
                profileInAlignedText = String.format("\n\t\t\t%s", profile.replaceAll("\n", "\n\t\t\t"));
            System.out.println(profileInAlignedText); // just making the text in cli aligned with others
            pause();
            System.out.println("------------------------- Edit --------------------------------------\n");
            System.out.println("\t\t\t1. Change Username\n\t\t\t2. Change Password\n\t\t\t3. Change First Name\n\t\t\t4. Change Last Name\n\t\t\t5. Add Avatar");
            String choice = scanner.next();
            switch(choice) {
                case "1":
                    String newUsername = scanner.next();
                    break;
            }
            pause();

        }
        catch(ForbiddenException fx) {
            System.out.println("This action needs you to be logged into your account!");
                throw new AuthenticationRequiredException();
        }
    }
}
