package cooking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.simple.JSONArray;

import com.jogamp.opengl.GL2;

public class CookingSystem {

    private static int nextAvailableID = 0;

    ArrayList<InProgressDish> inProgressDishes = new ArrayList<InProgressDish>();

    private static CookingPot cookingPot = new CookingPot(21,11);

    // Slot 1: pan
    // Slot 2: pot
    // Slot 3: cutting board
    private static String[][] toolSlots = new String[3][5];

    public static void setup(){

    }
    public static void update(){
        cookingPot.update();
    }
    public static void updateRequiringGL(GL2 gl){
        cookingPot.updateRequiringGL(gl);
    }
    


    public static String getResult(ArrayList<String> ingredients) {
        ArrayList<String> dishesArray = ItemData.getDishesArray();
        for(int i = 0; i < dishesArray.size(); i++){
            String dishName = dishesArray.get(i);
            JSONArray dishIngredients = (JSONArray) ItemData.getItemProperty(dishName, "ingredients");
            ArrayList<String> dishArrayList = (ArrayList<String>) dishIngredients;
            
            Collections.sort(ingredients); 
            Collections.sort(dishArrayList);
            if(dishArrayList.equals(ingredients) && dishIngredients.size() == ingredients.size()){
                return dishName;
            }
        }
        return null;
    }
    public static CookingPot getCookingPot(){
        return cookingPot;
    }
    public static void addIngredientToPot(String ingredient){
        cookingPot.addIngredient(ingredient);
    }
    public static int getNextAvailableID(){
        nextAvailableID ++;
        return nextAvailableID - 1;
    }
    public static String[][] getToolSlots() {
        return toolSlots;
    }
}
