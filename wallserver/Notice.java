package wallserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.security.SecureRandom;
import java.util.*;
import java.util.regex.Pattern;

public class Notice {
    private String title, description, location, phoneNumber, email;
    private long price, id;
    private final ArrayList<String> images = new ArrayList<>();
    private static final HashMap<Long, Notice> notices = new HashMap<>();
    private static final SecureRandom secureRandom = new SecureRandom();
    private String ownerUsername;
    private Date lastChangeDate;
    public static final String DIR_NOTICES = "notices";
    private static ArrayList<String> locations = new ArrayList<>();


    public static HashMap<Long, Notice> getNotices() {
        return notices;
    }

    public static ArrayList<Notice> getNotices(String desiredLocation) {
        ArrayList<Notice> filteredNotices = new ArrayList<>();
        String rawLocation = desiredLocation.toLowerCase();
        for(Long key: notices.keySet()) {
            Notice n = notices.get(key);
            if(rawLocation.equals(n.getLocation().toLowerCase()))
                filteredNotices.add(n);
        }
        return filteredNotices;
    }

    public Date getLastChangeDate() { return this.lastChangeDate; }
    public void setLastChangeDate(Date date) {
        this.lastChangeDate = date;
    }

    public void updateLastChangeDate() {
        this.lastChangeDate = new Date();
    }

    private Notice(long id, String ownerUsername, String title, long price, String description, String location, String phoneNumber, String email) throws BadInputException {
        this.setID(id);
        this.setOwnerUsername(ownerUsername);
        this.setTitle(title);
        this.setDescription(description);
        this.setPrice(price);
        this.setLocation(location);
        this.setPhoneNumber(phoneNumber);
        this.setEmail(email);

        addLocationToList(location);
    }

    public static ArrayList<String> getLocations() {
        return locations;
    }
    private static void addLocationToList(String location) {
        final String rawLocation = location.toLowerCase();
        final int length = locations.size();
        int pos = 0;
        for(pos = 0; pos < length; pos++) {
            int result = rawLocation.compareTo(locations.get(pos).toLowerCase());
            if(result == 0)
                return;
            if(result < 0)
                break;
        }
        locations.add(pos, location);

    }

    public String getOwnerUsername() {
        return this.ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
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

    public void setEmail(String email) throws BadInputException {
        if(!Pattern.matches("[a-zA-Z0-9.]*[@]{1}.[a-zA-Z0-9.]*", email))
            throw new BadInputException("Wrong Email: Email must be in standard email format.");
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

    public void setPhoneNumber(String phoneNumber) throws BadInputException {
        if(!Pattern.matches("[[+]?\\d+]{11,}", phoneNumber))
            throw new BadInputException("Wrong PhoneNumber: phone number must be in valid form!");
        this.phoneNumber = phoneNumber;
    }

    public void addImage(String url) throws NotFoundException {
        // TODO: check url => if url not found return false
        if(!Tools.isImageUrlValid(url))
            throw new NotFoundException(url);
        this.images.add(url);
    }


    public static Notice add(Account owner, String title, long price,
                             String description, String location, String phoneNumber,
                             String email, String images) throws IOException, BadInputException {
        load(); // make sure notices hash map is loaded
        long newID = -1;
        for(newID = secureRandom.nextLong();
            newID < 0 || Tools.fileExists(DIR_NOTICES, String.valueOf(newID));
            newID = secureRandom.nextLong()); // file new id that isn't used before


        Notice newOne = new Notice(newID, owner.getUsername(), title, price, description, location, phoneNumber, email);
        newOne.updateLastChangeDate();

        if(images != null && !images.equals("") && !images.equals("-"))
        {
            String[] imageUrls = images.split("\n");
            for(String url: imageUrls){
                try {
                    newOne.addImage(url);
                } catch(NotFoundException ignored) {
                    System.out.printf("Image: %s was not found and was ignored and rejected by server! It can not be added to image list of the notice !\n");
                }
            }
        }

        newOne.save();
        owner.updateNotices(newOne);
        notices.put(newOne.getID(), newOne);
        return newOne;
    }

    public static ArrayList<Notice> search(String subString) {
        ArrayList<Notice> result = new ArrayList<>();
        String rawSubString = subString.toLowerCase();
        for(Long key: notices.keySet()) {
            Notice n = notices.get(key);
            if(n.getTitle().toLowerCase().contains(rawSubString))
                result.add(n);
        }
        return result;
    }

    public void save() throws IOException {
        Tools.makeSureDirectoryExists(DIR_NOTICES);

        FileWriter fwNotice = new FileWriter(String.format("./%s/%s.dat", DIR_NOTICES, this.id));
        // write data
        // line1: ownerUsername username,
        fwNotice.write(this.ownerUsername + "\n");
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
        // line8: last change date
        fwNotice.write(this.lastChangeDate.getTime() + "\n");

        // next lines: images
        for(String image: this.images) {
            fwNotice.write(image  + "\n");
        }
        fwNotice.close();
    }

    // TODO: edit these
    // TODO: add images in Notice.add
    public String toString() {
        return String.format("%20d\t%20s\t%10d", this.id, this.title, this.price);
    }

    public String toString(boolean fullDescription) {
        if(!fullDescription)
            return this.toString();
        StringBuilder strImages = new StringBuilder(this.images.size() > 0 ? "{\n" : "-");

        for(String image: images)
            strImages.append("\t").append(image).append(", \n");

        if(this.images.size() > 0)
            strImages.append("\n }");
        return String.format(". ID: \t%d\n. Title: \t%s\n. Price: \t%d\n. Location: %s\n. Owner: \t%s" +
                        "\n\t. Phone Number: %s\n\t. Email: %s\n. Description: %s\n. Images: %s\n. Last Change on: %s",
                this.id, this.title, this.price, this.location, this.ownerUsername,
                this.phoneNumber, this.email, this.description, strImages.toString(), this.getLastChangeDate().toString());
    }

    public static Notice get(long id) throws NotFoundException {
        if(notices.isEmpty())
            load();
        if(!notices.containsKey(id))
            throw new NotFoundException(String.valueOf(id));
        return notices.get(id);
    }
    public static Notice load(long id) throws FileNotFoundException, CorruptedDataException {
        File fileNotice = new File(String.format("./%s/%d.dat", DIR_NOTICES, id));
        Scanner fileScanner = new Scanner(fileNotice);
        ArrayList<String> fields = new ArrayList<>();
        while(fileScanner.hasNextLine())
            fields.add(fileScanner.nextLine());

        Notice notice;
        if(fields.size() < 8)
            throw new CorruptedDataException("This notice data file has been modified illegally!");
        try {
            notice = new Notice(id, fields.get(0), fields.get(1), Long.parseLong(fields.get(2)), fields.get(3),
                    fields.get(4), fields.get(5), fields.get(6));
            notice.setLastChangeDate(new Date(Long.parseLong(fields.get(7)))); // CHECK THIS: I WANT TO DO CONVERT STRING TO DATE
        }
        catch(NumberFormatException | BadInputException nfx) {
            throw new CorruptedDataException("This notice data file has been modified illegally!");
        }

        // images
        for(int i = 8; i < fields.size(); i++){
            notice.images.add(fields.get(i));
        }
        fileScanner.close();
        return notice;
    }

    public static void load() {
        if(!notices.isEmpty())
            return;
        locations.clear();
        if(Tools.makeSureDirectoryExists("./" + DIR_NOTICES)){
            File noticesFolder = new File("./" + DIR_NOTICES);
            for(File noticeFile: Objects.requireNonNull(noticesFolder.listFiles())){
                try {
                    long id = Long.parseLong(noticeFile.getName().replaceAll(".dat", ""));
                    Notice notice = load(id);
                    notices.put(id, notice);
                } catch(Exception ignored) {}
            }
            System.out.println("Loading notices completed ...");
        }
    }
}
