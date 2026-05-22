import java.awt.event.KeyEvent;

public class Player {
    public enum PlayerState {
        IDLE,
        DASHING,
        BLOCKING,
        ATTACKING
    }
    private static double xPos = 0;
    private static double yPos = 0.5;
    private static int xDir = 0;
    private static int yDir = 0;
    private static double xVel = 0;
    private static double yVel = 0;
    public static int lastDash = (int)System.currentTimeMillis() - 420;
    private static int lastBlock = (int)System.currentTimeMillis() - 420;

    private static PlayerState playerState = PlayerState.IDLE;

    public static void update(Input input){

        /////// MOVEMENT
        
        // Base movement
        if(input.getKey(KeyEvent.VK_W)){
            yVel -= 0.015;
            yDir = -1;
        }
        
        if(input.getKey(KeyEvent.VK_A)){
            xVel -= 0.015;
            xDir = -1;
        }
        
        if(input.getKey(KeyEvent.VK_S)){
            yVel += 0.015;
            yDir = 1;
        }
        
        if(input.getKey(KeyEvent.VK_D)){
            xVel += 0.015;
            xDir = 1;
        }

        // Dashing logic & cooldown
        if(input.getKey(KeyEvent.VK_SHIFT) && (int) System.currentTimeMillis() - lastDash > 1000){
            double xAdd = 0;
            double yAdd = 0;
            if(input.getKey(KeyEvent.VK_W)){
                yAdd -= 0.2;
                yDir = -1;
            }
            
            if(input.getKey(KeyEvent.VK_A)){
                xAdd -= 0.2;
                xDir = -1;
            }
            
            if(input.getKey(KeyEvent.VK_S)){
                yAdd += 0.2;
                yDir = 1;
            }
            
            if(input.getKey(KeyEvent.VK_D)){
                xAdd += 0.2;
                xDir = 1;
            }

            double magnitude = Math.sqrt(xAdd * xAdd + yAdd * yAdd);

            if(magnitude != 0){
                yAdd /= magnitude;
                xAdd /= magnitude;
            }
            yAdd *= 0.4;
            xAdd *= 0.4;

            lastDash = (int) System.currentTimeMillis();
            xVel += xAdd;
            yVel += yAdd;

            //Sounds.playSound("Roll");
        }
                // Dashing logic & cooldown
        if(input.getKey(KeyEvent.VK_SPACE) && (int) System.currentTimeMillis() - lastBlock > 1000){
            lastBlock = (int) System.currentTimeMillis();

            //Sounds.playSound("Blocking");
        }

        xPos += xVel;
        yPos += yVel;
        
        xVel *= 0.85;
        yVel *= 0.85;

        ///////// PLAYER STATES
        
        if((int)System.currentTimeMillis() - lastDash < 400){
            playerState = PlayerState.DASHING;
        }
        else if((int)System.currentTimeMillis() - lastBlock < 400){
            playerState = PlayerState.BLOCKING;
        } 
        else {
            playerState = PlayerState.IDLE;
        }
    }
    public static PlayerState getState(){
        return playerState;
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
    public static int getLastDash() {
        return lastDash;
    }
    public static int getLastBlock() {
        return lastBlock;
    }
    
    
}
