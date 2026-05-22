//-------------------------------------------------//
//    Noah2           Imports                      //
//-------------------------------------------------// 
import java.io.File;
import java.util.ArrayList;
import java.io.IOException;
import javax.sound.sampled.*;
import java.util.Random;

//-------------------------------------------------//
//    b               Sounds                       //
//-------------------------------------------------// 
public class Sounds {

    static boolean isHeadless = false;
    
    //Create list
    private static File[] soundFiles;
    private static String[] soundNames;
    private static AudioInputStream[] soundAudio;
    private static Clip[] soundClip;
    private static ArrayList<File> tempsoundFiles = new ArrayList<File>();
    private static ArrayList<String> tempSoundNames = new ArrayList<String>();

    static Random random = new Random();

    public Sounds(){

        //Load the sounds
        recursiveSoundLoad("snd");

        //Set the size of the Sound classes
        soundFiles = new File[tempsoundFiles.size()];
        soundNames = new String[tempSoundNames.size()];
        soundClip = new Clip[tempSoundNames.size()];
        soundAudio = new AudioInputStream[tempsoundFiles.size()];

        try{

            // Load tempSoundList into SoundList
            for(int i = 0; i < soundFiles.length; i++){
                soundFiles[i] = tempsoundFiles.get(i);
                soundAudio[i] = AudioSystem.getAudioInputStream(soundFiles[i]);
            }


            // Load tempSoundNames into soundNames
            for(int i = 0; i < soundNames.length; i++){
                soundNames[i] = tempSoundNames.get(i);
                soundClip[i] = AudioSystem.getClip();
                if(soundNames[i].equals("Countryside")){
                    
                }
            }

        }
        //Cacth for the program
        catch (IOException | LineUnavailableException | UnsupportedAudioFileException error) {

            //If something hapeens print it out
            error.printStackTrace();
        }
    }


    //SoundLoading Method
    private static void recursiveSoundLoad(String folderPath){
        // New file from the path
        File folder = new File(folderPath);
        // Get the contents of the folder
        File[] contents = folder.listFiles();
        // If folder is actually a folder (not an sound)...
        if (contents != null) {
            if(!folder.toString().endsWith(".wav")){
                // Search the contents of the folder
                for(int i = 0; i < contents.length; i++){
                    recursiveSoundLoad(contents[i].getAbsolutePath());
                }
            }
        }
        // If folder is an sound...
        else {
            // ImageIO error handling
            try {
                // Add this sound to the list
                tempsoundFiles.add(folder);
                // Add its name to the list of names
                tempSoundNames.add(folder.getName().replace(".wav", ""));
            } catch(Exception e){
                // ImageIO shouldn't throw errors bruh
                System.out.println("This REALLY shouldn't happen. SOUND EDITION");
            }
        }
    }

    /*
     * //Play Sound Method
public static void playSound(String name) {
    try {
        // Loop through sound names to find a match
        for (int i = 0; i < soundNames.length; i++) {
            if (soundNames[i].equals(name)) {

                // If the clip is already open, reset it
                if (soundClip[i].isOpen()) {
                    soundClip[i].stop();
                    soundClip[i].flush();
                    soundClip[i].setFramePosition(0);
                } else {
                    soundClip[i].open(soundAudio[i]);  // Open only if not already opened
                }
                
                soundClip[i].start();
                break;  // Exit loop once sound is found and played
            }
        }
    } catch (IOException | LineUnavailableException error) {
        error.printStackTrace();
    }
}

     * 
     */


    //Play Sound Method
    public static void playSound(String name){
        if(!isHeadless){
            try{

                //For every SoundName
                for(int i = 0; i < soundNames.length; i++){

                    //If the SoundName equal the Given Soundname
                    if(soundNames[i].equals(name)){

                        if (soundClip[i].isOpen()) {
                            soundClip[i].stop();
                            soundClip[i].flush();
                            soundClip[i].setFramePosition(0);
                        } else {
                            soundClip[i].open(soundAudio[i]);  // Open only if not already opened
                            if(soundNames[i].equals("Countryside")){
                                soundClip[i].setFramePosition(
                                    random.nextInt(soundClip[i].getFrameLength())
                                );
                            }
                        }

                        soundClip[i].start();
                        break;  // Exit loop once sound is found and played

                    }
                }
            }

            //Cacth for the program
            catch (IOException | LineUnavailableException error) {

                //If something hapeens print it out
                error.printStackTrace();
            }
        }
    }
    
    //Stop Sound Method
    public static void stopSound(String name){

        //For every SoundName
        for(int i = 0; i < soundNames.length; i++){

            //If the SoundName equal the Given Soundname
            if(soundNames[i].equals(name)){

                //Stop the Clip
                soundClip[i].flush();
                soundClip[i].stop();
            }
        }
    }
}