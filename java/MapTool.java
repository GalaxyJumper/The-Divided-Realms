
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MapTool {
    private static boolean running = false;
    private static int framesLastSecond = 0;
    public static int framesThisSecond = 0;
    private static long lastSecondTime = System.nanoTime();
    private static long now;
    public static Input input;
    public static int currentChunkX = 0;
    public static int currentChunkY = 0;
    public static int currentTileX = 0;
    public static int currentTileY = 0;
    public static String currentTileType = "grass";
    public static int lastKeyPress = (int)System.currentTimeMillis() - 500;
    public static boolean wasMapEditied = false;
    static Scanner sc = new Scanner(System.in);
    public static void start () {

        Thread thread = new Thread() {

            public void run (){
                running = true;
                input = new Input();
                MapToolGUI.init();
                //new Sounds();
                //Sounds.playSound("Countryside");
                while(running) {
                    now = System.nanoTime();
                                             // 1 second
                    if(now > lastSecondTime + 1000000000){
                        lastSecondTime = now;
                        framesLastSecond = framesThisSecond;
                        framesThisSecond = 0;
                        System.out.println(framesLastSecond);
                    }
                    updateEditingPositions();
                    Camera.update(currentChunkX * 10 + 5, currentChunkY * 10 + 5);
                    MapToolGUI.renderGame();
                    Player.update(input);

                    framesThisSecond ++;
                }
            }
        };
        thread.setName("Map Tool");
        thread.start();
        // rectangle collisions:

        // DETECTION:
        //
        // graph collisions:

        // for every point:
        //  if(point < mx + b and is within bounds){
        //      everything move by   
        //  }
    }
    public static void updateEditingPositions(){
        wasMapEditied = false;
        if((int)System.currentTimeMillis() - lastKeyPress > 100){
            if(input.getKey(KeyEvent.VK_LEFT)){
                currentChunkX -= 1;
            }
            
            if(input.getKey(KeyEvent.VK_RIGHT)){
                currentChunkX += 1;
            }
            
            if(input.getKey(KeyEvent.VK_UP)){ 
                currentChunkY -= 1;
            }
            
            if(input.getKey(KeyEvent.VK_DOWN)){
                currentChunkY += 1;
            }
            currentChunkX = (int)clamp(currentChunkX, 0, Map.currentMap().widthChunks);
            currentChunkY = (int)clamp(currentChunkY, 0, Map.currentMap().heightChunks);
        
            if(input.getKey(KeyEvent.VK_A)){
                currentTileX -= 1;
            }

            
            if(input.getKey(KeyEvent.VK_D)){
                currentTileX += 1;
            }
            
            if(input.getKey(KeyEvent.VK_W)){ 
                currentTileY -= 1;
            }
            
            if(input.getKey(KeyEvent.VK_S)){
                currentTileY += 1;
            }
            currentTileX = (int)clamp(currentTileX, 0, 9);
            currentTileY = (int)clamp(currentTileY, 0, 9);

            if(input.getKey(KeyEvent.VK_R)){
                Map.currentMap().getChunk(currentChunkX, currentChunkY).editHeight(currentTileX, currentTileY, 1);
                wasMapEditied = true;
            }
            
            if(input.getKey(KeyEvent.VK_F)){
                Map.currentMap().getChunk(currentChunkX, currentChunkY).editHeight(currentTileX, currentTileY, -1);
                wasMapEditied = true;
            }
            if(input.getKey(KeyEvent.VK_T)){
                Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffHeight(currentTileX, currentTileY, 1);
                wasMapEditied = true;
            }
            if(input.getKey(KeyEvent.VK_G)){
                Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffHeight(currentTileX, currentTileY, -1);
                wasMapEditied = true;
            }
            if(input.getKey(KeyEvent.VK_ENTER)){
                System.out.println("Tile type: ");
                while(!sc.hasNextLine()){
                    sc.nextLine();
                }
                String newTile = sc.nextLine();
                currentTileType = newTile;
            }
            if(input.getKey(KeyEvent.VK_S) && input.getKey(KeyEvent.VK_CONTROL)){
                Map.currentMap().saveMap();
            }
            
            lastKeyPress = (int)System.currentTimeMillis();
        }
        
        if(input.getKey(KeyEvent.VK_SPACE)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editTile(currentTileX, currentTileY, currentTileType);
            wasMapEditied = true;
        }
        if(input.getKey(KeyEvent.VK_NUMPAD1)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "sw");
            wasMapEditied = true;
        }
        
        if(input.getKey(KeyEvent.VK_NUMPAD2)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "s");
            wasMapEditied = true;
        }
        if(input.getKey(KeyEvent.VK_NUMPAD3)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "se");
            wasMapEditied = true;
        }
        if(input.getKey(KeyEvent.VK_NUMPAD6)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "e");
            wasMapEditied = true;
        }
        if(input.getKey(KeyEvent.VK_NUMPAD9)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "ne");
            wasMapEditied = true;
        }
        if(input.getKey(KeyEvent.VK_NUMPAD8)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "n");
            wasMapEditied = true;
        }
        if(input.getKey(KeyEvent.VK_NUMPAD7)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "nw");
            wasMapEditied = true;
        }
        if(input.getKey(KeyEvent.VK_NUMPAD4)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "w");
            wasMapEditied = true;
            System.out.println("edited cliff direction to w");
        }
        
        if(input.getKey(KeyEvent.VK_NUMPAD5)){
            Map.currentMap().getChunk(currentChunkX, currentChunkY).editCliffDirection(currentTileX, currentTileY, "none");
            wasMapEditied = true;
        }
    }
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
    public static double dist(double x1, double y1, double x2, double y2){
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
    public static int currentChunkX(){
        return currentChunkX;
    }
    
    public static int currentChunkY(){
        return currentChunkY;
    }
    

}
