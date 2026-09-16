package cooking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.simple.JSONArray;

public class CookingSystem {

    ArrayList<InProgressDish> inProgressDishes = new ArrayList<InProgressDish>();

    private static CookingPot cookingPot = new CookingPot(21,11);

    public static void setup(){

    }
    public static void update(){
        cookingPot.update();
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
}
