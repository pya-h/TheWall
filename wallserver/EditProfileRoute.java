package wallserver;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.net.HttpURLConnection;

public class EditProfileRoute extends PostRequestHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        super.handle(httpExchange);
        Account account = null;
        try {
            String token = this.parameters.get("token").toString(),
                    field = this.parameters.get("field").toString().toLowerCase(),
                    value = this.parameters.get("value").toString();
            account = Account.getAccountByToken(token);
            switch (field) {
                case "username":
                    account.setUsername(value);
                    break;
                case "firstname":
                    account.setFirstName(value);
                    break;
                case "lastname":
                    account.setLastName(value);
                    break;
                case "password":
                    String password = this.parameters.get("password").toString();
                    if (!account.comparePassword(password))
                        throw new WrongCredentialsException();
                    account.setPassword(value);
                    break;

                case "avatar":
                    // TODO: check image exists on the net	h
                    account.addAvatar(value);
                    break;
                default:
                    throw new BadRequestException("User attempts to change a profile field that doesnt exists!");
            }
            account.save();
            System.out.printf("%s changed his/her %s successfully!\n", account.getUsername(), field);
            this.sendResponse(HttpURLConnection.HTTP_OK, account.toString());
        }
        catch(NotFoundException nfx) {
            System.out.println(nfx.getMessage());
            this.sendResponse(HttpURLConnection.HTTP_NOT_FOUND);
        }
        catch (BadInputException bix) {
            System.out.println(bix);
            sendResponse(HttpURLConnection.HTTP_NOT_ACCEPTABLE);
        }
        catch(WrongTokenException wtx) {
            // TODO: send proper message to client
            System.out.println(wtx.getMessage());
            sendResponse(HttpURLConnection.HTTP_FORBIDDEN);
        }
        catch (WrongCredentialsException wcx) {
            System.out.printf("User:%s attempted to password change without entering the correct current password!%n", account != null ? account.getUsername() : "{???}");
            sendResponse(HttpURLConnection.HTTP_UNAUTHORIZED);
        } catch (BadRequestException e) {
            System.out.println(e.getMessage());
            sendResponse(HttpURLConnection.HTTP_BAD_REQUEST);
        }
        catch(IOException iox) {
            // Todo: send response
            System.out.println(iox.getMessage());
        }
    }
    
}