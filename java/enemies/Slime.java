package enemies;
import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import game.Player;
import gui.Renderer;
import game.GameLoop;
import gui.Images;

public class Slime extends Enemy{
    private double[] pathfindingTarget = new double[2];

    private int lastTargetUpdate = (int) System.currentTimeMillis() - 5000;
    private int timeTilNextTargetUpdate = 4500;

    private int lastAggro = (int) System.currentTimeMillis() - 10000;

    private int aggroTime = 5000;

    private Texture[] spritesheet;
    private Texture[] shadowSheet;

    private int animationTimeOffset = (int)(Math.random() * 1500.0);

    public Slime(double x, double y){
        super(x, y);
    }

    @Override
    public void update() {

        int now = (int) System.currentTimeMillis();

        xPos += xVel * GameLoop.deltaTime; 
        yPos += yVel * GameLoop.deltaTime;

        yVel *= 1 - (0.05 * GameLoop.deltaTime);
        xVel *= 1 - (0.05 * GameLoop.deltaTime);

        hitbox = new double[]{xPos - 0.5, yPos - 0.5, 1, 1};

        double distanceToTarget = GameLoop.dist(xPos, yPos, pathfindingTarget[0], pathfindingTarget[1]);
        distanceToTarget = Math.min(2, distanceToTarget);
        // Set velocity as a vector pointing in the direction of the target
        yVel += 0.001 * (pathfindingTarget[1] - yPos) * distanceToTarget * GameLoop.deltaTime;
        xVel += 0.001 * (pathfindingTarget[0] - xPos) * distanceToTarget * GameLoop.deltaTime;

        int currentFrame = (int)Math.floor(Math.abs(now + animationTimeOffset) / 100 % 7);

        // On frame 1 the slime is touching the ground and so that is when we update the target pose. 
        // Also slow down for that slime jump effect.

        if(currentFrame >= 1 && currentFrame <= 2 && !(now - lastHit < 300)){
            xVel *= 0.25;
            yVel *= 0.25;
            if(GameLoop.dist(xPos, yPos, Player.getxPos(), Player.getyPos()) < 4) lastAggro = now;
            if(now - lastAggro < aggroTime && GameLoop.dist(xPos, yPos, Player.getxPos(), Player.getyPos()) < 9){
                pathfindingTarget[0] = Player.getxPos();
                pathfindingTarget[1] = Player.getyPos();
            } else {
                pathfindingTarget[0] = xPos + Math.random() * 10 - 5;
                pathfindingTarget[1] = yPos + Math.random() * 10 - 5;
            }
        }
        for(int i = 0; i < GameLoop.getEnemies().size(); i++){
            // Separate slimes from each other if they are too close together
            if(GameLoop.getEnemies().get(i) != this && GameLoop.getEnemies().get(i).getClass().getSimpleName().equals("Slime")){
                if(GameLoop.dist(xPos, yPos, GameLoop.getEnemies().get(i).getxPos(), GameLoop.getEnemies().get(i).getyPos()) < 1){
                    double distanceToOther = GameLoop.dist(xPos, yPos, GameLoop.getEnemies().get(i).getxPos(), GameLoop.getEnemies().get(i).getyPos());
                    xVel += 0.002 * (xPos - GameLoop.getEnemies().get(i).getxPos()) / distanceToOther * GameLoop.deltaTime;
                    yVel += 0.002 * (yPos - GameLoop.getEnemies().get(i).getyPos()) / distanceToOther * GameLoop.deltaTime;
                }
            }
        }
        if(this.health <= 0){ // die

            GameLoop.getEnemies().remove(this);
        }
    }
    @Override
    public void applyDamage(int amount){
        this.health -= amount;
        this.lastHit = (int) System.currentTimeMillis();
    }
    public int getLastHitTime(){
        return lastHit;
    }
    public void loadTextures(GLProfile glprofile){
        
        images = new Images("img/enemies", GLProfile.getDefault());
        spritesheet = Images.readSpriteSheet(images.getImage("slime"), glprofile, 2, 4);
        shadowSheet = new Texture[spritesheet.length];
        for(int i = 0; i < shadowSheet.length; i++){
            shadowSheet[i] = Renderer.toGlassTexture(
                Images.readSpriteSheetToBufferedImage(images.getImage("slime"), glprofile, 2, 4)[i]
            );
        }
    }
    public void draw(GL2 gl){



        int spritesheetIndex = Math.abs((int)System.currentTimeMillis() + animationTimeOffset) / 100 % 7;

        if(lastHit + 300 > (int)System.currentTimeMillis()){
            gl.glColor3f(1f, 0.0f, 0.0f);
            spritesheetIndex = (int)Math.floor(
                ((int)System.currentTimeMillis() - lastHit) / 150
            ) + 1;
        } else {
            gl.glColor3f(1f, 1f, 1f);
        }

        Renderer.textureQuad(   
            gl, spritesheet[spritesheetIndex], 
            new float[] {(float)xPos,        1f,   (float)yPos - 0.38f}, 
            new float[] {(float)xPos + 1f, 1f,   (float)yPos - 0.38f},
            new float[] {(float)xPos + 1f, 0f, (float)yPos},
            new float[] {(float)xPos,        0f, (float)yPos}
        );
        
        Renderer.textureQuad(
            gl, shadowSheet[spritesheetIndex],
            new float[] {(float)xPos + 1f,        0f,   (float)yPos + 1f}, 
            new float[] {(float)xPos, 0f,   (float)yPos + 1f},
            new float[] {(float)xPos, 0f, (float)yPos},
            new float[] {(float)xPos + 1f,        0f, (float)yPos}
        );
        // reset tint back to normal
        gl.glColor3f(1f, 1f, 1f);
    }
}
