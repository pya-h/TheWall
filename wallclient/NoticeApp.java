package wallclient;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class NoticeApp extends ClientApp {
    public static long newNotice() throws HttpExceptions, IOException {
        String response = "";

        try {
            // System.out.println("Now enter your notice info in the next section...");
            // pause();
            clearScreen();

            String title, description, location, phoneNumber, email, imageUrl;
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

            while(!Pattern.matches("[[+]?\\d+]{11,}", phoneNumber)) {
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
            StringBuilder images = new StringBuilder();
            imageUrl = "";
            while(imageUrl != null && !imageUrl.equals("-")) {
                System.out.print("Image (Enter '-' to end input): ");
                imageUrl = scanner.nextLine();
                if(imageUrl.equals("-"))
                    break;
                try {
                    System.out.println("Please wait until url check is completed...");
                    ImageIO.read(new URL(imageUrl));
                    images.append(imageUrl).append("\n");
                } catch (IOException ex) {
                    System.out.println("There is no such image! Please enter a valid url !");
                }
            }

            HashMap<String, String> data = new HashMap<>();
            data.put("token", loginToken);
            data.put("title", title);
            data.put("price", String.valueOf(price));
            data.put("description", description);
            data.put("location", location);
            data.put("phoneNumber", phoneNumber);
            data.put("email", email);
            data.put("images", images.toString().equals("") ? "-" : images.toString());

            response = post("notices/+", data);
            return Long.parseLong(response.replace("\n", ""));
        } catch (UnauthorizedException ex) {
            System.out.println("ERROR: Username or password is incorrect!");
        } catch(NotAcceptableException nax) {
            System.out.println("Server rejected to create the notice because the data you provided doesn't have the standard pattern!");
        }
        return -1;
    }

    public static long[] listNotices(String res) {
        System.out.printf("%4s%10s%10s\t%20s\t%10s\n", "#", "ID", "", "Title", "Price");
        System.out.println("-----------------------------------------------------------------");
        if(res == null || res.equals("") || res.replaceAll("\n", "").equals("-")){
            System.out.println("\t\tNo result !");
            return null;
        }
        String[] rows = res.split("\n");
        long[] IDs = new long[rows.length];
        for(int i = 0; i < rows.length; i++){
            System.out.printf("%4d%s\n", i + 1, rows[i]);
            String[] columns = rows[i].split("\t");
            IDs[i] = Long.parseLong(columns[0].replaceAll(" ", ""));
            // number format exceptions happens?
        }
        return IDs;
    }

    public static long obtainNotices(String res, String menuTitle, HashMap<String, String> params) throws IOException, HttpExceptions {
        clearScreen();
        System.out.printf("------------------------- %s ---------------------------------\n", menuTitle);
        long[] IDs = listNotices(res);
        if(IDs == null || IDs.length == 0)
            return -1;
        String answer;
        do {
            System.out.print("Do you want to sort/filter the result? (Y/N)");
            answer = scanner.next().toLowerCase();
        } while(!answer.equals("n") && !answer.equals("y"));

        if(answer.equals("y")) {
            System.out.println("\t\t\t1. Sort by Price (Ascending) \n\t\t\t2. Sort by Price (Descending)" +
                    "\n\t\t\t3. Sort by Last Change Date (Ascending) \n\t\t\t4. Sort by Last Change Date (Descending)" +
                    "\n\t\t\t5. Set Minimum Price\n\t\t\t6. Set Maximum Price");
            answer = scanner.next();
            switch(answer) {
                case "1":
                    params.put("sort", "+p");
                    break;
                case "2":
                    params.put("sort", "-p");
                    break;
                case "3":
                    params.put("sort", "+l");
                    break;
                case  "4":
                    params.put("sort", "-l");
                    break;
                case  "5":
                    long min = -1;
                    while(min < 0) {
                        try {
                            System.out.print("Enter minimum price: ");
                            min = scanner.nextLong();
                        }
                        catch(NumberFormatException nfx) {
                            min = -1;
                        }
                    }
                    params.put("min", String.valueOf(min));
                    break;
                case  "6":
                    long max = -1;
                    while(max < 0) {
                        try {
                            System.out.print("Enter maximum price: ");
                            max = scanner.nextLong();
                        } catch(NumberFormatException nfx) {
                            max = -1;
                        }
                    }
                    params.put("max", String.valueOf(max));
                    break;
                default:
                    System.out.print("Wrong answer! ");
                    pause();
                    break;
            }
            res = post("notices", params);
            return obtainNotices(res, menuTitle, params);
        }

        System.out.print("Select the notice you want to view: ");
        int position = scanner.nextInt();
        while(position <= 0 || position > IDs.length) {
            try {
                System.out.print("Wrong choice! Select a valid notice: ");
                position = scanner.nextInt();
            }
            catch(NumberFormatException nfx) {
                position = -1;
            }
        }
        position--;
        return IDs[position];
    }

    public static void viewNoticesByLocation() throws IOException, HttpExceptions {
        clearScreen();
        System.out.println("------------------------- Locations ---------------------------------\n");
        String res = get("locations");
        String[] locations = res.split("\n");
        for(int i = 0; i < locations.length; i++)
            System.out.printf("%d. %s\n", i + 1, locations[i]);
        System.out.println();
        System.out.print("Select your location: ");

        int position = scanner.nextInt();
        while(position <= 0 || position > locations.length) {
            try {
                System.out.print("Wrong choice! Select a valid location: ");
                position = scanner.nextInt();
            }
            catch(NumberFormatException nfx) {
                position = -1;
            }
        }
        position--;
        HashMap<String, String> data = new HashMap<>();
        data.put("token", loginToken);
        data.put("location", locations[position]);
        res = post("notices", data);

        // split ids for next input
        long selectedID = obtainNotices(res, locations[position], data);
        data.clear();
        data.put("token", loginToken);
        data.put("id", String.valueOf(selectedID));
        res = post("notices/get", data);
        showNotice(res, selectedID);

    }

    private static void showNotice(String res, long noticeID) throws IOException, HttpExceptions {
        clearScreen();
        System.out.println("----------------------- Notice View ----------------------------");
        String singleNoticeAlignedText = String.format("\n\t\t\t%s", res.replaceAll("\n", "\n\t\t\t"));
        System.out.println(singleNoticeAlignedText); // just making the text in cli aligned with others
        System.out.print("F: Add/Remove notice to/from your favorite list ");
        String answer = scanner.next().toLowerCase();
        if(answer.equals("f")) {
            HashMap<String, String> data = new HashMap<>();
            data.put("token", loginToken);
            data.put("id", String.valueOf(noticeID));
            res = post("notices/fav", data);
            System.out.println(res);
        }
    }

    public static void menu() throws AuthenticationRequiredException {
        while(true) {
            try {
                clearScreen();
                System.out.println("------------------------- Notices -----------------------------------\n");
                System.out.println("\t\t\t1. Add New Notice\n\t\t\t2. View Notices\n\t\t\t3. Search Notices\n\t\t\t4. Favorite List\n\t\t\t0. Back");
                System.out.print("GO: ");
                String choice = scanner.next();
                scanner = new Scanner(System.in); // prevent bug that will be cause when using .next, .nextLine, .nextInt abd .,..
                switch(choice) {
                    case "1":
                        long noticeID = newNotice();
                        if(noticeID >= 0)
                            System.out.println("Notice: " + noticeID + " successfully added!");
                        else
                            System.out.println("Failed to add your notice! Please try again with correct information ...");
                        break;
                    case "2":
                        viewNoticesByLocation();
                        break;
                    case "3":
                        noticeSearch();
                        break;
                    case "4":
                        loadFavorites();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Wrong destination! please try again...");
                }
            } catch (NotFoundException nfx) {
                System.out.println("This specific notice you're reaching for, doesn't exist!");
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

    private static void loadFavorites() throws IOException, HttpExceptions {
        HashMap<String, String> data = new HashMap<>();
        data.put("token", loginToken);
        data.put("section", "fav");
        String res = post("notices", data);
        long selectedID = obtainNotices(res, "Favorites", data);
        if(selectedID >= 0) {
            data.put("token", loginToken);
            data.put("id", String.valueOf(selectedID));
            res = post("notices/get", data);
            showNotice(res, selectedID);

        }
    }
    private static void noticeSearch() throws IOException, HttpExceptions {
        clearScreen();
        System.out.println("------------------------- Search ---------------------------------\n");

        System.out.print("Search: ");

        String text = scanner.nextLine();
        HashMap<String, String> data = new HashMap<>();
        data.put("token", loginToken);
        data.put("search", text);
        String res = post("notices", data);
        long selectedID = obtainNotices(res, "Search: " + text, data);
        if(selectedID >= 0) {
            data.put("token", loginToken);
            data.put("id", String.valueOf(selectedID));
            res = post("notices/get", data);
            showNotice(res, selectedID);

        }
    }
}
