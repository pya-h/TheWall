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
			
			System.out.println("Server is listening on localhost:" + PORT);
			httpServer.createContext("/", new RootRoute());
			httpServer.createContext("/login", new LoginRoute());
			httpServer.createContext("/register", new RegisterRoute());
			httpServer.setExecutor(Executors.newCachedThreadPool());
			httpServer.start();
		}
		catch(IOException ex) {
			System.out.println(ex.getMessage());
		}

	}
}