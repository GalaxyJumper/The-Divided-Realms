package cooking;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
public class ItemData {
    static JSONObject jsonObject = null;
    static JSONArray itemsArray = null; 
    static ArrayList<String> ingredientsArray = new ArrayList<String>();
    static ArrayList<String> dishesArray = new ArrayList<String>();
    static JSONParser parser = new JSONParser();
    public static void readDataFromFile(){
        try {
            jsonObject = (JSONObject) parser.parse(new FileReader("data/ItemData.json"));
            itemsArray = (JSONArray) jsonObject.get("items");
            for(int i = 0; i < itemsArray.size(); i++){
                JSONObject item = (JSONObject) itemsArray.get(i);
                
                if(item.get("type").equals("ingredient")){
                    ingredientsArray.add((String) item.get("name"));

                } else if(item.get("type").equals("dish")){
                    dishesArray.add((String) item.get("name"));
                    
                } else if(item.get("type").equals("dishIngredient")){
                    ingredientsArray.add((String) item.get("name"));
                    dishesArray.add((String) item.get("name"));

                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }
    public static String[] getItemData(int index) {
        if (index < 0 || index >= itemsArray.size()) {
            return null;
        }
        JSONObject item = (JSONObject) itemsArray.get(index);
        String[] data = new String[item.size()];
        int i = 0;
        for (Object key : item.keySet()) {
            data[i] = (String) item.get(key);
            i++;
        }
        return data;
    }
    public static String[] getItemData(String name) {
        for (int i = 0; i < itemsArray.size(); i++) {
            JSONObject item = (JSONObject) itemsArray.get(i);
            if (item.get("name").equals(name)) {
                String[] data = new String[item.size()];
                int j = 0;
                for (Object key : item.keySet()) {
                    data[j] = (String) item.get(key);
                    j++;
                }
                return data;
            }
        }
        return null;
    }
    public static Object getItemProperty(String name, String propertyName) {
        for (int i = 0; i < itemsArray.size(); i++) {
            JSONObject item = (JSONObject) itemsArray.get(i);
            if (item.get("name").equals(name)) {
                return item.get(propertyName);
            }
        }
        return null;
    }
    public static ArrayList<String> getIngredientsArray() {
        return ingredientsArray;
    }
    public static ArrayList<String> getDishesArray() {
        return dishesArray;
    }
     
}