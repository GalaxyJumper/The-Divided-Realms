
import java.util.ArrayList;

import cooking.ItemData;
import game.GameLoop;
import maptool.MapTool;
public class Main {
    public static void main(String[] args) { 
        if(GameLoop.mapToolMode){
            MapTool.start();
        } else {
            GameLoop.start();
        }
    }
}
