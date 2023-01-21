package wallserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Account {
    private String username, password, firstName, lastName, loginToken;
    private final ArrayList<String> avatars = new ArrayList<>();
    private static final HashMap<String, Account> loggedInAccounts = new HashMap<>();

    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();
    public static final String DIR_ACCOUNTS = "accounts";
    private final ArrayList<Long> noticeList = new ArrayList<>();
    private final ArrayList<Long> favorites = new ArrayList<>();

    private static final String SALT = "dghGHJS7891!@j";
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) throws BadInputException {
        if(!Pattern.matches("[a-zA-Z0-9._]*", username))
            throw new BadInputException("username contains illegal characters!");
        Tools.deleteFile(DIR_ACCOUNTS, this.username);
        this.username = username;
    }
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) throws BadInputException {
        if(!Pattern.matches("[a-z0-9]{8,}", password))
            throw  new BadInputException("Password must be at least 8 characters and only use numbers and a-z...");
        else {
            char[] pass = password.toCharArray();
            boolean hasBinary = false;
            int numberOfAs = 0;
            for(int i = 0; i < pass.length && numberOfAs < 2 && !hasBinary; i++) {
                if(pass[i] == 'a')
                    numberOfAs++;
                if(pass[i] == '0' || pass[i] == '1')
                    hasBinary = true;
                if(i < pass.length - 1) {
                    if(pass[i] + 1 == pass[i + 1] || pass[i + 1] + 1 == pass[i])
                        throw new BadInputException("Pass must not contain consecutive numbers!");
                }
            }
            if(!hasBinary && numberOfAs < 2)
                throw new BadInputException("Password must has at least 2 'a' character or a binary digit!");

        }
        this.password = SALT + password + SALT;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        // TODO: check firstname => if error => method not allowed

        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        // TODO: check lastname => if error => method not allowed

        this.lastName = lastName;
    }

    public static String newToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static Account getAccountByToken(String token) throws WrongTokenException {
        Account acc = loggedInAccounts.get(token);
        if(acc == null)
            throw new WrongTokenException(token);
        return acc;
    }

    public void addAvatar(String url) throws NotFoundException {
        // TODO: check url => if url not found return false
        if(!Tools.isImageUrlValid(url))
            throw new NotFoundException(url);
        this.avatars.add(url);
    }
    private Account(String username, String password) throws BadInputException {
        this.setUsername(username);
        this.setPassword(password);
        this.firstName = "-";
        this.lastName = "-";
    }
    private Account(String username, String password, boolean noSalt) throws BadInputException {
        this.setUsername(username);
        if(noSalt)
            this.password = password;
        else
            this.setPassword(password);

        this.firstName = "-";
        this.lastName = "-";
    }
    public static Account login(String username, String password) throws WrongCredentialsException, CorruptedDataException, BadInputException {
        if(!Tools.fileExists(DIR_ACCOUNTS, username))
            throw new WrongCredentialsException();
        try {
            File fileUser = new File(String.format("./%s/%s.dat", DIR_ACCOUNTS, username));
            Scanner fileScanner = new Scanner(fileUser);
            if(!fileScanner.hasNextLine()){
                fileScanner.close();
                throw new WrongCredentialsException();
            }
            final String actualPassword = fileScanner.nextLine(),
                saltedInputPassword = SALT + password + SALT;
            if(!actualPassword.equals(saltedInputPassword)) {
                fileScanner.close();
                throw new WrongCredentialsException();
            }
            Account account = new Account(username, actualPassword, true);
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

    public boolean comparePassword(String pass) {
        return this.password.equals(SALT + pass + SALT);
    }

    public void load() throws FileNotFoundException, CorruptedDataException {
        File file = new File(String.format("./%s/%s.dat", DIR_ACCOUNTS, this.username));
        Scanner fileScanner = new Scanner(file);
        ArrayList<String> fields = new ArrayList<>();
        while(fileScanner.hasNextLine())
            fields.add(fileScanner.nextLine());

        final int numberOfFields = fields.size();
        if(numberOfFields < 1){
            fileScanner.close();

            throw new CorruptedDataException("This user's data is corrupted somehow!");
        }
        if(this.password == null || this.password.equals(""))
            this.password = fields.get(0);
        this.setFirstName(numberOfFields >= 2 ? fields.get(1) : "-");
        this.setLastName(numberOfFields >= 3 ? fields.get(2) : "-");
        // avatars
        for(int i = 3; i < numberOfFields; i++){
            try {
                this.addAvatar(fields.get(i));
            } catch(NotFoundException ignored) {}
        }
        fileScanner.close();

        // load notice list
        file = new File(String.format("./%s/%s.notices", DIR_ACCOUNTS, this.username));
        fileScanner = new Scanner(file);
        while(fileScanner.hasNextLong())
            this.noticeList.add(fileScanner.nextLong());
        fileScanner.close();

        // load favorites list
        file = new File(String.format("./%s/%s.fav", DIR_ACCOUNTS, this.username));
        fileScanner = new Scanner(file);
        while(fileScanner.hasNextLong())
            this.favorites.add(fileScanner.nextLong());
        fileScanner.close();
    }

    public static Account register(String username, String password) throws UsernameExistsException, IOException, BadInputException {
        Account newOne = new Account(username, password);
        if(Tools.fileExists(DIR_ACCOUNTS, username))
            throw new UsernameExistsException(username);

        newOne.save();
        final String token = newToken();
        newOne.setLoginToken(token);
        loggedInAccounts.put(token, newOne);
        return newOne;
    }

    public void save() throws IOException {
        Tools.makeSureDirectoryExists(DIR_ACCOUNTS);

        FileWriter fwUser = new FileWriter(String.format("./%s/%s.dat", DIR_ACCOUNTS, this.username));
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
        this.saveNoticeList();
        this.saveFavorites();
    }

    public String toString() {
        StringBuilder strAvatars = new StringBuilder(this.avatars.size() > 0 ? "{\n" : "-");

        for(String avatar: avatars)
            strAvatars.append("\t").append(avatar).append(", \n");

        if(this.avatars.size() > 0)
            strAvatars.append("\n }");
        return String.format(". Username: \t%s\n. Firstname: \t%s\n. Lastname: \t%s\n. Avatars: %s",
            this.username, this.firstName, this.lastName, strAvatars.toString());
    }

    public void updateNotices(Notice notice) throws IOException {
        this.noticeList.add(notice.getID());
        this.saveNoticeList();
    }

    public void saveNoticeList() throws IOException {
        Tools.makeSureDirectoryExists(DIR_ACCOUNTS);

        FileWriter fwNoticeList = new FileWriter(String.format("./%s/%s.notices", DIR_ACCOUNTS, this.username));
        // write data

        for(long noticeID: this.noticeList) {
            fwNoticeList.write(noticeID + "\n");
        }
        fwNoticeList.close();
    }

    public boolean addToFavorites(long id) throws NotFoundException, IOException {
        Notice notice = Notice.get(id);
        boolean noticeExists = favorites.contains(id);
        if(!noticeExists) {
            favorites.add(id);
        }
        else {
            favorites.remove(id);
        }
        this.saveFavorites();
        return !noticeExists;

    }

    private void saveFavorites() throws IOException {
        Tools.makeSureDirectoryExists(DIR_ACCOUNTS);

        FileWriter fwNoticeList = new FileWriter(String.format("./%s/%s.fav", DIR_ACCOUNTS, this.username));
        // write data

        for(long noticeID: this.favorites) {
            fwNoticeList.write(noticeID + "\n");
        }
        fwNoticeList.close();
    }

    public ArrayList<Notice> getFavorites() {
        ArrayList<Notice> favs = new ArrayList<>();
        for(long id: this.favorites) {
            try {
                favs.add(Notice.get(id));
            } catch (NotFoundException ignored) { }
        }
        return favs;
    }

    public void logout() {
        if(loggedInAccounts.containsKey(this.loginToken))
            loggedInAccounts.remove(this.loginToken);
    }
}
