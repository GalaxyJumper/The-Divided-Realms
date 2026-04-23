import java.awt.image.BufferedImage;
public class Animation {
    private int startTime; // Animation start time
    private int widthFrames; // How many frames horizontally the sheet is wide
    private int heightFrames; // How many frames vertically the sheet is tall
    private int frameWidth; // How wide each frame is
    private int frameHeight;// Take a wild guess
    private int numFrames; // How many frames in the animation
    private int frameTime; // How long each frame is
    private boolean isLooping; // Does the animation loop?
    private int step; // Which step are we on?
    private BufferedImage sheet; // The sprite sheet

    public Animation(BufferedImage sheet, int widthFrames, int heightFrames, int numFrames, int frameTime, boolean isLooping){ 
        this.widthFrames = widthFrames;
        this.heightFrames = heightFrames;
        this.frameWidth = sheet.getWidth() / widthFrames;
        this.frameHeight = sheet.getHeight() / heightFrames;
        this.numFrames = numFrames;
        this.isLooping = isLooping;
        this.frameTime = frameTime;
        this.sheet = sheet;
        startTime = (int)System.currentTimeMillis(); // = now
        step = 0;
    }
    public void start(){
        startTime = (int)System.currentTimeMillis();
    }
    public void setStartTime(int time){
        startTime = time;
    }
    public BufferedImage getFrameNum(int num){
        int x = (num % widthFrames) * frameWidth;
        int y = (num / widthFrames) * frameHeight;
        return sheet.getSubimage(x, y, frameWidth, frameHeight);
    }
    public BufferedImage getFrame(){
        // Get current step
        step = (int)(((int)System.currentTimeMillis() - startTime) / frameTime);
        // Handle looping/animation end
        if(step >= numFrames){
            if(isLooping){
                startTime = (int)System.currentTimeMillis();
                step = 0;
            } else {
                step = numFrames - 1;
            }
        }
        // Determine which frame to draw
        int x = (step % widthFrames) * frameWidth;
        int y = (step / widthFrames) * frameHeight;
        return sheet.getSubimage(x, y, frameWidth, frameHeight);
    }
        public BufferedImage getFrame(int customTime
        ){
        // Get current step
        step = (int)(((int)System.currentTimeMillis() - customTime) / frameTime);
        // Handle looping/animation end
        if(step >= numFrames){
            if(isLooping){
                startTime = (int)System.currentTimeMillis();
                step = 0;
            } else { 
                step = numFrames - 1;
            }
        }
        // Determine which frame to draw
        int x = (step % widthFrames) * frameWidth;
        int y = (step / widthFrames) * frameHeight;
        return sheet.getSubimage(x, y, frameWidth, frameHeight);
    }

}
