package wallclient;

public class Client extends ClientApp {
    public static void main(String[] args) {
        while(true) {

            try {
                String res = get("");
                // Runtime.getRuntime().exec("clear");
                System.out.println(res);

                AuthenticateApp.menu();
                // now the user is logged in:
                clearScreen();
                while (true) {
                    System.out.println("--------------------- Main Menu ------------------------------------------\n");
                    System.out.println("\t\t\t1. Profile\n\t\t\t2. Notices");
                    System.out.print("GO: ");
                    String destination = scanner.next();
                    switch (destination) {
                        case "1":
                            ProfileApp.menu();
                            break;
                        case "2":
                            // show an d check notices
                            NoticeApp.menu();
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
            catch (Exception ex) {
                // do sth
                break;
            }
        }
        scanner.close();

    }
}
