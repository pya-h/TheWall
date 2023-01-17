package wallserver;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Tools {
    public static boolean isImageUrlValid(String url){
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            return image != null;
        } catch (IOException ignored) {}
        return false;
    }

    public static boolean makeSureDirectoryExists(String dir) {
        File directory = new File(String.format("./%s", dir));
        if(!directory.exists()) { // create directory if it doesn't exist
            directory.mkdir();
            return false;
        }
        return true;
    }

    public static boolean fileExists(final String parentDirectory, final String filename) {
        if(!makeSureDirectoryExists(parentDirectory))
            return false;
        File fileUser = new File(String.format("./%s/%s.dat", parentDirectory, filename));
        return fileUser.exists();
    }

    public static void deleteFile(String parentDirectory, String filename) {
        if(fileExists(parentDirectory, filename)) {
            File oldFile = new File(String.format("./%s/%s.dat", parentDirectory, filename));
            oldFile.delete();
        }
    }
}
