package wallserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Account {
    private String username, password, firstName, lastName, loginToken;
    private ArrayList<String> avatars = new ArrayList<>();
    private static Map<String, Account> loggedInAccounts = new HashMap<>();

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        // check username
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        // check password
        this.password = password;
    }
    
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        // check firstName
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        // check lastName
        this.lastName = lastName;
    }
    
    public static String newToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public boolean addAvatar(String url) {
        // TODO: check url => if url not found return false
        this.avatars.add(url);
        return true;
    }
    private Account(String username, String password) {
        this.username = username;
        this.password = password;
        this.firstName = "-";
        this.lastName = "-";
    }

    public static Account login(String username, String password) throws WrongCredentialsException, IOException, CorruptedDataException{
        if(!userExists(username))
            throw new WrongCredentialsException();
        try {
            File fileUser = new File(String.format("./accounts/%s.dat", username));
            Scanner fileScanner = new Scanner(fileUser);
            if(!fileScanner.hasNextLine()){
                fileScanner.close();
                throw new WrongCredentialsException();
            }
            final String actualPassword = fileScanner.nextLine();
            if(!actualPassword.equals(password)) {
                fileScanner.close();
                throw new WrongCredentialsException();
            }
            Account account = new Account(username, actualPassword);
            account.load();
            final String token = newToken();
            account.setLoginToken(token);
            loggedInAccounts.put(token, account);
            fileScanner.close();
            return account;
        }
        catch(FileNotFoundException ex) {
            throw new WrongCredentialsException();
        }
    }

    public String getLoginToken() {
        return this.loginToken;
    }
    public void setLoginToken(String token) {
        this.loginToken = token;
    }

    public void load() throws FileNotFoundException, CorruptedDataException {
        File fileUser = new File(String.format("./accounts/%s.dat", this.username));
        Scanner fileScanner = new Scanner(fileUser);
        ArrayList<String> fields = new ArrayList<>();
        while(fileScanner.hasNextLine())
            fields.add(fileScanner.nextLine());
        final int numberOfFields = fields.size();
        if(numberOfFields < 1){
            fileScanner.close();
            throw new CorruptedDataException("This user's data is corrupted somehow!");
        }    
        if(this.password == null || this.password.equals(""))
            this.setPassword(fields.get(0));
        this.setFirstName(numberOfFields >= 1 ? fields.get(1) : "-");
        this.setFirstName(numberOfFields >= 2 ? fields.get(2) : "-");
        // avatars
        for(int i = 3; i < numberOfFields; i++) 
            this.addAvatar(fields.get(i));
        fileScanner.close();
    }
    public static boolean userExists(final String username) {
        File dirAccounts = new File("./accounts");
        if(!dirAccounts.exists()) {
            dirAccounts.mkdir();
        }
        File fileUser = new File(String.format("./accounts/%s.dat", username));
        return fileUser.exists();
    }
    
    public static Account register(String username, String password) throws UsernameExistsException, IOException {
        // TODO: check password
        // TODO: check users
        Account newOne = new Account(username, password);
        if(userExists(username))
            throw new UsernameExistsException(username);
        
        newOne.save();
        final String token = newToken();
        newOne.setLoginToken(token);
        loggedInAccounts.put(token, newOne);
        return newOne;
    }

    public void save() throws IOException {
        File dirAccounts = new File("./accounts");
        if(!dirAccounts.exists()) {
            dirAccounts.mkdir();
        }
        FileWriter fwUser = new FileWriter(String.format("./accounts/%s.dat", this.username));
        // write data
        // line1: password, 
        fwUser.write(this.password + "\n");
        // optionals: line2: firstname, line3: lastName, next lines: avatars
        
        fwUser.write((this.firstName != null && !this.firstName.equals("") ? this.firstName : "-") + "\n");
        fwUser.write((this.lastName != null && !this.lastName.equals("") ? this.lastName : "-") + "\n");
        for(String avatar: this.avatars) {
            fwUser.write(avatar  + "\n");
        }
        fwUser.close();
    }
}
