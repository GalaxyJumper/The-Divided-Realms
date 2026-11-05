package cooking;

import java.util.ArrayList;

import cooking.cookingEvents.StirEvent;
import cooking.cookingEvents.*;
/*

    Data type for keeping track of dishes in progress.

*/
public class InProgressDish {
    private String dishName;

    private int totalCookTime;
    private int timeStarted;

    private double qualityTotal = 0;
    private double qualityCount = 0;

    private ArrayList<CookingEvent> cookingEvents = new ArrayList<CookingEvent>();

    private int[] eventTimes;

    private ArrayList<String> ingredients;


    public InProgressDish(String dishName, int cookTime, ArrayList<String> ingredients){
        this.dishName = dishName;
        this.totalCookTime = cookTime;
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
    public int getTotalCookTime(){ return totalCookTime; }

    public double getQuality(){
        if(qualityCount == 0) return 1;
        return qualityTotal / qualityCount;
    };
    public void addNewQualityValue(double newValue){
        qualityTotal += newValue;
        qualityCount ++;
    }
    public int getCookTime(){
        return 100;
    }
}
