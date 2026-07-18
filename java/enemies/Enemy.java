package enemies;
import gui.Renderer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;

import gui.Images;

public abstract class Enemy {
    protected double xPos;
    protected double yPos;
    protected double xVel;
    protected double yVel;

    protected double maxHealth = 100;

    protected double health;

    protected Images images;

    protected double[] hitbox = new double[]{xPos, yPos, 1, 1};

    public Enemy(double x, double y){
        xPos = x;
        yPos = y;
        xVel = 0;
        yVel = 0;
        health = 100;
    }
    public abstract void update();
    public abstract void draw(GL2 gl2);
    public abstract void loadTextures(GLProfile glprofile);
    public void applyDamage(int amount){
        this.health -= amount;
    }
    public void applyVelocity(double x, double y){
        this.xVel += x;
        this.yVel += y;
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
    public double[] getHitbox(){
        return hitbox;
    }


}