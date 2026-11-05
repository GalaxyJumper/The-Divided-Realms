package game;
import java.awt.MouseInfo;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.glu.GLU;

import cooking.CookingSystem;
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
    // Physics
    private static double xPos = 7;
    private static double yPos = 8.5;
    private static int xDir = 0;
    private static int yDir = 0;
    private static double xVel = 0;
    private static double yVel = 0;
    private static double xAdd = 0;
    private static double yAdd = 0;
    private static double xOnScreen = 0;
    private static double yOnScreen = 0;

    // Abilities/player state
    public static int lastDash = (int)System.currentTimeMillis() - 420;
    private static int lastBlock = (int)System.currentTimeMillis() - 420;
    private static int lastAttack = (int)System.currentTimeMillis() - 420;
    private static int lastSpecialAttack = (int)System.currentTimeMillis() - 420;
    private static int lastHit = (int) System.currentTimeMillis() - 4200;
    private static double[] attackHitbox = new double[]{0, 0, 0, 0};
    private static double[] hitbox = new double[] {0, 0, 0.8, 0.8};
    private static PlayerState playerState = PlayerState.IDLE;

    private static double health = 500;

    // Inventory
    private static ArrayList<String> inventory = new ArrayList<String>();


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
        if(input.getKey(KeyEvent.VK_ENTER) && GameLoop.dist(Player.getxPos(), Player.getyPos(), CookingSystem.getCookingPot().getXPos(), CookingSystem.getCookingPot().getYPos()) < 3){
            CookingSystem.getCookingPot().startCooking();

            //Sounds.playSound("Blocking");
        }

        xPos += xVel * GameLoop.deltaTime;
        yPos += yVel * GameLoop.deltaTime;

        xVel *= 1 - (0.15 * GameLoop.deltaTime);
        yVel *= 1 - (0.15 * GameLoop.deltaTime);

        hitbox[0] = xPos + 0.1; 
        hitbox[1] = yPos + 0.1;


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
        

        double mouseX = Input.getMouseX();
        double mouseY = Input.getMouseY();

        double magnitude = GameLoop.dist(xOnScreen, yOnScreen, mouseX, mouseY);
        xAdd = (mouseX - xOnScreen) / magnitude * 0.15;
        yAdd = (mouseY - yOnScreen) / magnitude * 0.15;
        // System.out.println(
        //     "Mouse pos: " + mouseX + "," + mouseY + "\n"
        //      + "Player pos: " + xOnScreen + ", " + yOnScreen + "\n"
        //       + "Viewport info: " + Arrays.toString(Renderer.getViewportSize()) + "\n" 
        //         + "Frame info: " + Renderer.getResolution().toString() + "\n");
        
        if(Player.getState() != PlayerState.SPECIAL_ATTACKING){
            attackHitbox = new double[]{
                GameLoop.clamp(Player.getxPos() + xAdd * 20, Player.getxPos() - 1, Player.getxPos() + 0.75) - 0.5, 
                GameLoop.clamp(Player.getyPos() + yAdd * 20, Player.getyPos() - 1.5, Player.getyPos() + 0.75) - 0.75, 
                2, 2};
        }
        if(Player.getState() == PlayerState.SPECIAL_ATTACKING){
            // make the player spin
            xDir = (int)Math.signum((int)System.currentTimeMillis() - lastSpecialAttack - 200);
            yDir = (int)Math.signum((int)System.currentTimeMillis() - lastSpecialAttack - 100);
        }
        

    }

    public static void updateRequiringGL(GL2 gl){
        double[] posOnScreen = Renderer.worldToScreen(xPos + 0.5, 0, yPos - 0.5, gl);
        xOnScreen = posOnScreen[0];
        yOnScreen = posOnScreen[1];
    }
    
    public static void handleMouseClick(MouseEvent e) {

        int[] viewportSize = Renderer.getViewportSize();
        double slotSize = 100.0 * (double)viewportSize[2] / 1080.0;
        double barStartX = viewportSize[2] / 2 - slotSize * 5;

        if(e.getButton() == MouseEvent.BUTTON1
        && e.getX() > barStartX 
        && e.getX() < barStartX + slotSize * 10 
        && e.getY() > viewportSize[3] - 50 - slotSize 
        && e.getY() < viewportSize[3] - 50 
        && GameLoop.dist(Player.getxPos(), Player.getyPos(), CookingSystem.getCookingPot().getXPos(), CookingSystem.getCookingPot().getYPos()) < 3
        ){

            int inventorySlot = (int)((e.getX() - barStartX) / slotSize);
            CookingSystem.addIngredientToPot(Player.getInventorySlot(inventorySlot));
            System.out.println(CookingSystem.getCookingPot().getIngredients().size());
            inventory.remove(inventorySlot);

            return;
        }
        
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
    public static void handleKeyPress(KeyEvent e){

        int[] viewportSize = Renderer.getViewportSize();
        int slotSize = (int)(100.0 * (double)viewportSize[2] / 1080.0);
        int barStartX = viewportSize[2] / 2 - slotSize * 5;

        System.out.println("Key typed: " + e.getKeyCode() + ", " + Input.mouseInRect(new Rectangle(barStartX, viewportSize[3] - slotSize * 2, slotSize * 10, slotSize)));

        if(e.getKeyCode() >= 49 && e.getKeyCode() <= 57 
        && Input.mouseInRect(new Rectangle(barStartX, viewportSize[3] - slotSize * 2, slotSize * 10, slotSize))){

            int slot = e.getKeyCode() - 49;
            System.out.println("SLOT: slot");
            
            if(inventory.size() - 1 >= slot){
                inventory.remove(slot);
            }

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

    public static int getLastHit() {
        return lastHit;
    }

    public static double[] getAttackHitbox() {
        return attackHitbox;
    }

    public static double[] getHitbox() {
        return hitbox;
    }

    public static void applyDamage(double amount){
        health -= amount;
        lastHit = (int) System.currentTimeMillis();
    }

    public static void applyVelocity(double xAmount, double yAmount){
        xVel += xAmount;
        yVel += yAmount;
    }

    public static double getHealth(){
        return health;
    }

    public static ArrayList<String> getInventory() {
        return inventory;
    }
    public static void addToInventory(String item) {
        inventory.add(item);
    }

    public static void removeFromInventory(String item) {
        inventory.remove(item);
    }

    public static String getInventorySlot(int index) {
        if(index < 0 || index >= inventory.size()){
            return null;
        }
        return inventory.get(index);
    }
    
}
