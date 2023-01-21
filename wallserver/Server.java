package wallserver;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import com.sun.net.httpserver.*;

public class Server {
	public static final int PORT = 7001;
	public static void main(String[] args) {
		try {
			HttpServer httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            Notice.load();
            System.out.println("Server is listening on localhost:" + PORT + " ...");
            httpServer.createContext("/", new RootRoute());
			httpServer.createContext("/locations", new GetLocationsRoute());
			httpServer.createContext("/notices", new GetNoticesRoute());
			httpServer.createContext("/notices/get", new GetSingleNoticeRoute());
			httpServer.createContext("/login", new LoginRoute());
			httpServer.createContext("/register", new RegisterRoute());
			httpServer.createContext("/profile/view", new ViewProfileRoute());
			httpServer.createContext("/profile/edit", new EditProfileRoute());
			httpServer.createContext("/notices/+", new AddNoticeRoute());
			httpServer.createContext("/notices/fav", new AddToFavoritesRoute());
			httpServer.createContext("/logout", new LogoutRoute());
			httpServer.setExecutor(Executors.newCachedThreadPool());
			httpServer.start();
		}

		catch(IOException ex) {
			System.out.println(ex.getMessage());
		}

	}
}