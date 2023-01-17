package wallserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Notice {
    private String title, description, location, phoneNumber, email;
    private long price, id;
    private final ArrayList<String> images = new ArrayList<>();
    private static final HashMap<Integer, Notice> all = new HashMap<>();
    private static final SecureRandom secureRandom = new SecureRandom();
    private Account owner;
    public static final String DIR_NOTICES = "notices";

    private Notice(long id, Account owner, String title, long price, String description, String location, String phoneNumber, String email) {
        this.setID(id);
        this.setOwner(owner);
        this.setTitle(title);
        this.setDescription(description);
        this.setPrice(price);
        this.setLocation(location);
        this.setPhoneNumber(phoneNumber);
        this.setEmail(email);
    }

    public Account getOwner() {
        return this.owner;
    }

    public void setOwner(Account owner) {
        this.owner = owner;
    }

    public long getPrice() {
        return this.price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getID() {
        return this.id;
    }

    public void setID(long id) {
        this.id = id;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        // TODO: check email format
        this.email = email;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void addImage(String url) throws NotFoundException {
        // TODO: check url => if url not found return false
        if(!Tools.isImageUrlValid(url))
            throw new NotFoundException(url);
        this.images.add(url);
    }

    public static Notice add(Account owner, String title, long price, String description, String location, String phoneNumber, String email) throws IOException {
        long newID = -1;
        for(newID = secureRandom.nextLong();
            newID < 0 || Tools.fileExists(DIR_NOTICES, String.valueOf(newID));
            newID = secureRandom.nextLong()); // file new id that isn't used before

        Notice newOne = new Notice(newID, owner, title, price, description, location, phoneNumber, email);
        newOne.save();
        owner.updateNotices(newOne);
        return newOne;
    }

    public void save() throws IOException {
        Tools.makeSureDirectoryExists(DIR_NOTICES);

        FileWriter fwNotice = new FileWriter(String.format("./%s/%s.dat", DIR_NOTICES, this.id));
        // write data
        // line1: owner username,
        fwNotice.write(this.owner.getUsername() + "\n");
        // line2: title
        fwNotice.write(this.title + "\n");
        // line3: price
        fwNotice.write(this.price + "\n");
        // line4: description
        fwNotice.write(this.description + "\n");
        // line5: location
        fwNotice.write(this.location + "\n");
        // line6: phone number
        fwNotice.write(this.phoneNumber + "\n");
        // line7: email
        fwNotice.write(this.email + "\n");

        // next lines: images
        for(String image: this.images) {
            fwNotice.write(image  + "\n");
        }
        fwNotice.close();
    }

    // TODO: edit these
    // TODO: there is no way to add images in the first place!
    // TODO: add images in Notice.add
    public String toString() {
        StringBuilder strAvatars = new StringBuilder(this.images.size() > 0 ? "{\n" : "-");

        for(String avatar: images)
            strAvatars.append("\t").append(avatar).append(", \n");

        if(this.images.size() > 0)
            strAvatars.append("\n }");
        return String.format(". Username: \t%s\n. Firstname: \t%s\n. Lastname: \t%s\n. Avatars: %s",
                this.title, this.location, this.phoneNumber, strAvatars.toString());
    }

    public void load() throws FileNotFoundException, CorruptedDataException {
        // TODO: load all notices
        File fileUser = new File(String.format("./accounts/%s.dat", this.title));
        Scanner fileScanner = new Scanner(fileUser);
        ArrayList<String> fields = new ArrayList<>();
        while(fileScanner.hasNextLine())
            fields.add(fileScanner.nextLine());
        final int numberOfFields = fields.size();
        if(numberOfFields < 1){
            fileScanner.close();
            throw new CorruptedDataException("This user's data is corrupted somehow!");
        }
        if(this.description == null || this.description.equals(""))
            this.setDescription(fields.get(0));
        this.setLocation(numberOfFields >= 2 ? fields.get(1) : "-");
        this.setPhoneNumber(numberOfFields >= 3 ? fields.get(2) : "-");
        // avatars
        for(int i = 3; i < numberOfFields; i++){
            try {
                this.addImage(fields.get(i));
            } catch(NotFoundException ignored) {}
        }
        fileScanner.close();
    }
}
