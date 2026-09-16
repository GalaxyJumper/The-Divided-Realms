package cooking;

import java.util.ArrayList;

import game.Player;

public class CookingPot {
    public enum State {
        EMPTY,
        FILLING,
        COOKING,
        DONE
    }

    State state = State.EMPTY;

    InProgressDish currentDish = null;

    ArrayList<String> ingredients = new ArrayList<String>();
    int xPos;
    int yPos;




    public CookingPot(int xTiles, int yTiles){
        this.xPos = xTiles;
        this.yPos = yTiles;
    }

    public void update(){
        if(state == State.COOKING){
            if(currentDish.getTimeStarted() + currentDish.getCookTime() < (int) System.currentTimeMillis()){
                state = State.EMPTY;
                Player.addToInventory(currentDish.getName());
                currentDish = null;
            }
        }
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
    public InProgressDish getCurrentDish(){
        return currentDish;
    }
}