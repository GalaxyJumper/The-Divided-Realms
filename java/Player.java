import java.awt.event.KeyEvent;

public class Player {
    private static double xPos = 0;
    private static double yPos = 0.5;
    private static int xDir = 0;
    private static int yDir = 0;
    private static double xVel = 0;
    private static double yVel = 0;
    private static int lastDash = Integer.MIN_VALUE;


    public static void update(Input input){
        if(input.getKey(KeyEvent.VK_W)){
            yVel -= 0.0005;
            yDir = -1;
        }
        
        if(input.getKey(KeyEvent.VK_A)){
            xVel -= 0.0005;
            xDir = -1;
        }
        
        if(input.getKey(KeyEvent.VK_S)){
            yVel += 0.0005;
            yDir = 1;
        }
        
        if(input.getKey(KeyEvent.VK_D)){
            xVel += 0.0005;
            xDir = 1;
        }
        if(input.getKey(KeyEvent.VK_SHIFT) && (int) System.currentTimeMillis() - lastDash > 1000){
            if(input.getKey(KeyEvent.VK_W)){
                yVel -= 0.03;
                yDir = -1;
            }
            
            if(input.getKey(KeyEvent.VK_A)){
                xVel -= 0.03;
                xDir = -1;
            }
            
            if(input.getKey(KeyEvent.VK_S)){
                yVel += 0.03;
                yDir = 1;
            }
            
            if(input.getKey(KeyEvent.VK_D)){
                xVel += 0.03;
                xDir = 1;
            }
            lastDash = (int) System.currentTimeMillis();
        }

        xPos += xVel;
        yPos += yVel;
        
        xVel *= 0.9;
        yVel *= 0.9;
    }

    public static double getxPos() {
        return xPos;
    }
    public static double getyPos() {
        return yPos;
    }
    public static int getxDir() {
        return xDir;
    }
    public static int getyDir() {
        return yDir;
    }
    public static double getxVel() {
        return xVel;
    }
    public static double getyVel() {
        return yVel;
    }
    
    
}
