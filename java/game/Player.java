package game;
import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import gui.Camera;
import gui.Renderer;

public class Player {
    public enum PlayerState {
        IDLE,
        DASHING,
        BLOCKING,
        ATTACKING,
        SPECIAL_ATTACKING
    }
    private static double xPos = 7;
    private static double yPos = 8.5;
    private static int xDir = 0;
    private static int yDir = 0;
    private static double xVel = 0;
    private static double yVel = 0;
    private static double xAdd = 0;
    private static double yAdd = 0;

    public static int lastDash = (int)System.currentTimeMillis() - 420;
    private static int lastBlock = (int)System.currentTimeMillis() - 420;
    private static int lastAttack = (int)System.currentTimeMillis() - 420;
    private static int lastSpecialAttack = (int)System.currentTimeMillis() - 420;
    private static double[] attackHitbox = new double[]{0, 0, 0, 0};
    private static PlayerState playerState = PlayerState.IDLE;

    public static void update(Input input){

        /////// MOVEMENT
        
        // Base movement
        if(input.getKey(KeyEvent.VK_W)){
            yVel -= 0.015 * GameLoop.deltaTime;
            yDir = -1;
        }
        
        if(input.getKey(KeyEvent.VK_A)){
            xVel -= 0.015 * GameLoop.deltaTime;
            xDir = -1;
        }
        
        if(input.getKey(KeyEvent.VK_S)){
            yVel += 0.015 * GameLoop.deltaTime;
            yDir = 1;
        }
        
        if(input.getKey(KeyEvent.VK_D)){
            xVel += 0.015 * GameLoop.deltaTime;
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

        xPos += xVel * GameLoop.deltaTime;
        yPos += yVel * GameLoop.deltaTime;

        xVel *= 1 - (0.15 * GameLoop.deltaTime);
        yVel *= 1 - (0.15 * GameLoop.deltaTime);

        ///////// PLAYER STATES
        
        if((int)System.currentTimeMillis() - lastDash < 400){
            playerState = PlayerState.DASHING;
        }
        
        else if((int)System.currentTimeMillis() - lastBlock < 400){
            playerState = PlayerState.BLOCKING;
        } 
        else if((int)System.currentTimeMillis() - lastAttack < 400){
            playerState = PlayerState.ATTACKING;
        } 
        else if((int)System.currentTimeMillis() - lastSpecialAttack < 400){
            playerState = PlayerState.SPECIAL_ATTACKING;
        }
        else {
            playerState = PlayerState.IDLE;
        }
        double estimatedXOnScreen = (Renderer.getWidth() / 2.0) - (Camera.getX() - Player.getxPos()) * 55.0 + 20;
        double estimatedYOnScreen = (Renderer.getHeight() / 2.0) - (Camera.getY() - Player.getyPos()) * 7.0*6.43 + 1230;

        double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
        double mouseY = MouseInfo.getPointerInfo().getLocation().getY() - 100;

        double magnitude = GameLoop.dist(estimatedXOnScreen, estimatedYOnScreen, mouseX, mouseY);
        xAdd = (mouseX - estimatedXOnScreen) / magnitude * 0.15;
        yAdd = (mouseY - estimatedYOnScreen) / magnitude * 0.15;
        if(Player.getState()!= Player.PlayerState.SPECIAL_ATTACKING){
            attackHitbox = new double[]{
                GameLoop.clamp(Player.getxPos() + xAdd * 20, Player.getxPos() - 1, Player.getxPos() + 0.75) - 0.5, 
                GameLoop.clamp(Player.getyPos() + yAdd * 20, Player.getyPos() - 1.5, Player.getyPos() + 0.75) - 0.75, 
                2, 2};
        }
    }
    
    public static void handleMouseClick(MouseEvent e) {
        
        if(e.getButton() == MouseEvent.BUTTON1){
            xVel += xAdd * GameLoop.deltaTime;
            yVel += yAdd * GameLoop.deltaTime;

            xDir = (int)Math.signum(xAdd);
            yDir = (int)Math.signum(yAdd);

            playerState = PlayerState.ATTACKING;
            lastAttack = (int) System.currentTimeMillis();
        } else if (e.getButton() == MouseEvent.BUTTON3){
            playerState = PlayerState.SPECIAL_ATTACKING;
            lastSpecialAttack = (int) System.currentTimeMillis();
            attackHitbox = new double[]{
                Player.getxPos() - 1.5,
                Player.getyPos() - 2,
                4, 
                4
            };
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
    public static int getLastAttack() {
        return lastAttack;
    }
    public static int getLastSpecialAttack() {
        return lastSpecialAttack;
    }
    public static double[] getAttackHitbox() {
        return attackHitbox;
    }
    
}
