package cooking;

import java.util.ArrayList;

public class InProgressDish {
    private String dishName;
    private int cookTime;
    private int timeStarted;
    private ArrayList<String> ingredients;

    public InProgressDish(String dishName, int cookTime, ArrayList<String> ingredients){
        this.dishName = dishName;
        this.cookTime = cookTime;
        this.timeStarted = (int) (System.currentTimeMillis());
        this.ingredients = ingredients;
    }
    public void setTimeStarted(int timeStarted){
        this.timeStarted = timeStarted;
    }
    public String getName(){
        return dishName;
    }
    public int getTimeStarted(){ return timeStarted; }
    public int getCookTime(){ return cookTime; }
}
