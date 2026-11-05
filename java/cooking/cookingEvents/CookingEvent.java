package cooking.cookingEvents;

public abstract class CookingEvent{

    protected int lengthMillis;

    protected double finishedQuality;

    public abstract void update();

    public abstract boolean isFinished();

    public abstract double getFinishedQuality();

}