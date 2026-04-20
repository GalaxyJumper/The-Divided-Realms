import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.spec.ECFieldF2m;
import java.util.ArrayList;
import javax.imageio.ImageIO;

import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Images {
    BufferedImage[] imageList;
    String[] imageNames;

    ArrayList<BufferedImage> tempImageList = new ArrayList<BufferedImage>();
    ArrayList<String> tempImageNames = new ArrayList<String>();

    public Images(String folderPath){
        //Load the images (see recursiveImageLoad)
        recursiveImageLoad(folderPath);

        imageList = new BufferedImage[tempImageList.size()];
        imageNames = new String[tempImageNames.size()];

        // Load tempImageList into imageList
        for(int i = 0; i < imageList.length; i++)
            imageList[i] = tempImageList.get(i);
        // Load tempImageNames into imageNames
        for(int i = 0; i < imageNames.length; i++)
            imageNames[i] = tempImageNames.get(i);
    }
    // Load images via a depth-first search for them.
    private void recursiveImageLoad(String folderPath){
        // New file from the path
        File folder = new File(folderPath);
        System.out.println(folderPath);
        // If folder is actually a folder (not an image)...
        if(!folder.toString().endsWith(".png")){
            // Get the contents of the folder
            File[] contents = folder.listFiles();
            // Search the contents of the folder for images
            for(int i = 0; i < contents.length; i++){
                recursiveImageLoad(contents[i].getAbsolutePath());
            }
        }
        // If folder is an image...
        else {
            // ImageIO error handling
            try {
                // Add this image to the list
                tempImageList.add(ImageIO.read(folder));
                // Add its name to the list of names
                tempImageNames.add(folder.getName().replace(".png", ""));
            } catch(Exception e){
                // ImageIO shouldn't throw errors bruh
                System.out.println("This REALLY shouldn't happen.");
            }
        }
    }
    public BufferedImage getImage(String name){
        for(int i = 0; i < imageNames.length; i++){
            if(imageNames[i].equals(name)){
                return imageList[i];
            }
        }
        return null;
    }
    public BufferedImage getImage(int id){
        return imageList[id];
    }
    public Texture getTexture(String name){
        for(int i = 0; i < imageNames.length; i++){
            if(imageNames[i].equals(name)){
                return AWTTextureIO.newTexture(GLProfile.getDefault(), imageList[i], false);
            }
        }
        return null;
    }
    public static Texture[] readSpriteSheet(BufferedImage image, GLProfile profile, int rows, int columns){
    
        Texture[] result = new Texture[rows*columns];
        for(int i = 0; i < rows; i++){
            for(int k = 0; k < columns; k++){
                result[i*columns + k] = AWTTextureIO.newTexture(
                    profile, 
                    image.getSubimage(
                        (image.getWidth() / columns) * k, 
                        (image.getHeight() / rows) * i, 
                        (image.getWidth() / columns), 
                        (image.getHeight() / rows)
                    ),
                    false
                );
            }   
        }
        return result;
    }
    public static BufferedImage[] readSpriteSheetToBufferedImage(BufferedImage image, GLProfile profile, int rows, int columns){
    
        BufferedImage[] result = new BufferedImage[rows*columns];
        for(int i = 0; i < rows; i++){
            for(int k = 0; k < columns; k++){
                result[i*columns + k] = 
                    image.getSubimage(
                        (image.getWidth() / columns) * k, 
                        (image.getHeight() / rows) * i, 
                        (image.getWidth() / columns), 
                        (image.getHeight() / rows)
                    );
            }   
        }
        return result;
    }

}