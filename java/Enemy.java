abstract class Enemy {
    protected double xPos;
    protected double yPos;
    protected double xVel;
    protected double yVel;

    protected double maxHealth = 100;

    protected double health;

    public Enemy(double x, double y){
        xPos = x;
        yPos = y;
        xVel = 0;
        yVel = 0;
        health = 100;
    }
    public abstract void update();

    public void applyDamage(int amount){
        this.health += amount;
    }

    public double getxPos() {
        return xPos;
    }
    public double getyPos() {
        return yPos;
    }
    public double getHealth(){
        return health;
    }


}