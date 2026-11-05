package cooking;

import java.util.ArrayList;

import com.jogamp.opengl.GL2;

import cooking.cookingEvents.StirEvent;
import game.Player;
import gui.Renderer;

public class CookingPot {
    public enum State {
        EMPTY,
        FILLING,
        COOKING,
        COOKING_EVENT,
        DONE
    }

    int potID;

    State state = State.EMPTY;

    InProgressDish currentDish = null;

    ArrayList<String> ingredients = new ArrayList<String>();
    int xPos;
    int yPos;

    int xPosOnscreen = 0;
    int yPosOnscreen = 0;



    public CookingPot(int xTiles, int yTiles){
        this.xPos = xTiles;
        this.yPos = yTiles;
        this.potID = CookingSystem.getNextAvailableID();
    }

    public void update(){
        if(state == State.COOKING){
            // If cooking time is up
            if(currentDish.getTimeStarted() + currentDish.getTotalCookTime() < (int) System.currentTimeMillis()){
                state = State.EMPTY;
                Player.addToInventory(currentDish.getName());
                currentDish = null;
            }
        } else if (state == State.COOKING_EVENT){
          //  currentCookingEvent.update();
        }
    }
    public void updateRequiringGL(GL2 gl){
        double[] posOnscreen = Renderer.worldToScreen(xPos, 0, yPos, gl);
        xPosOnscreen = (int) posOnscreen[0];
        yPosOnscreen = (int) posOnscreen[1];
    }
    public void addIngredient(String ingredient){
        if(ItemData.getItemProperty(ingredient, "type").equals("ingredient")){
            ingredients.add(ingredient);
        } else {
            return;
        }
        if(state == State.EMPTY){
            state = State.FILLING;
        }
    }
    public void startCooking(){
        System.out.println("Started cooking: cooking pot id " + this.getID());
        if(state == State.FILLING){
            String result = CookingSystem.getResult(ingredients);
            if(result != null){
                int cookTime = Integer.parseInt((String) ItemData.getItemProperty(result, "cookTime"));
                currentDish = new InProgressDish(result, cookTime, ingredients);
                currentDish.setTimeStarted((int) (System.currentTimeMillis()));
                ingredients.clear();
                state = State.COOKING;
            } else {
                for(int i = 0; i < ingredients.size(); i++){
                    Player.addToInventory(ingredients.get(i));
                }
                ingredients.clear();
                state = State.EMPTY;
            }
        }
    }

    public int getID(){
        return potID;
    }

    public State getState(){
        return state;
    }
    public ArrayList<String> getIngredients(){
        return ingredients;
    }
    public int getXPos(){
        return xPos;
    }
    public int getYPos(){
        return yPos;
    }
    public int getXOnScreen(){
        return xPosOnscreen;
    }
    public int getYOnScreen(){
        return yPosOnscreen;
    }
    public InProgressDish getCurrentDish(){
        return currentDish;
    }
}