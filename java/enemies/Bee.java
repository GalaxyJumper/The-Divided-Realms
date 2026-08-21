package enemies;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.jogamp.math.geom.plane.AffineTransform;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;

import game.Player;
import gui.Renderer;
import game.GameLoop;
import gui.Images;

public class Bee extends Enemy{
    private double[] pathfindingTarget = new double[2];

    private double lastDirectionUpdate = (int) System.currentTimeMillis() - 8000;
    private double timeTilNextDirectionUpdate = 5;
    double xAccel = 0.001;
    double yAccel = 0.001;


    private int lastAggro = (int) System.currentTimeMillis() - 10000;
    private int aggroTime = 5000;

    Images enemyImages;
    private Texture[] spritesheet;
    private Texture[] shadowSheet;
    private int animationTimeOffset = (int)(Math.random() * 1500.0);

    public Bee(double x, double y){
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

        if(now - lastDirectionUpdate > timeTilNextDirectionUpdate){
            xAccel = Math.random() * 0.002 - 0.001;
            
            yAccel = Math.random() * 0.002 - 0.001;

            lastDirectionUpdate = now;

            timeTilNextDirectionUpdate = Math.random() * 1000 + 500;
        }

        double distanceToTarget = GameLoop.dist(xPos, yPos, pathfindingTarget[0], pathfindingTarget[1]);
        
        // Close to the player: reset aggro timer.
        if(GameLoop.dist(xPos, yPos, Player.getxPos(), Player.getyPos()) < 5) lastAggro = now;
        
        // Aggroed: set target to the player and move in that direction.
        if(now - lastAggro < aggroTime && GameLoop.dist(xPos, yPos, Player.getxPos(), Player.getyPos()) < 5){
            pathfindingTarget[0] = Player.getxPos();
            pathfindingTarget[1] = Player.getyPos();
            
            distanceToTarget = Math.max(0.5, distanceToTarget);

            // Set velocity as a vector pointing in the direction of the target
            yVel += (0.006 * (pathfindingTarget[1] - yPos) / distanceToTarget) * GameLoop.deltaTime;
            xVel += (0.004 * (pathfindingTarget[0] - xPos) / distanceToTarget) * GameLoop.deltaTime;
        }
        // Non aggroed: move around. 
        else {
            yVel += yAccel * GameLoop.deltaTime;
            xVel += xAccel * GameLoop.deltaTime;

            xVel = GameLoop.clamp(xVel, -0.05, 0.05);
            
            yVel = GameLoop.clamp(yVel, -0.05, 0.05);
        }
        for(int i = 0; i < GameLoop.getEnemies().size(); i++){
            if(GameLoop.getEnemies().get(i) != this && GameLoop.getEnemies().get(i).getClass().getSimpleName().equals("Bee")){
                if(GameLoop.dist(xPos, yPos, GameLoop.getEnemies().get(i).getxPos(), GameLoop.getEnemies().get(i).getyPos()) < 1){
                    double distanceToOther = GameLoop.dist(xPos, yPos, GameLoop.getEnemies().get(i).getxPos(), GameLoop.getEnemies().get(i).getyPos());
                    xVel += (0.002 * (xPos - GameLoop.getEnemies().get(i).getxPos()) / distanceToOther) * GameLoop.deltaTime;
                    yVel += (0.002 * (yPos - GameLoop.getEnemies().get(i).getyPos()) / distanceToOther) * GameLoop.deltaTime;
                }
            }
        }
        if(this.health <= 0){ // die
            GameLoop.getEnemies().remove(this);
        }

    }
    public void loadTextures(GLProfile glprofile){
        
        images = new Images("img/enemies", GLProfile.getDefault());
        spritesheet = Images.readSpriteSheet(images.getImage("bee"), glprofile, 8, 3);
        shadowSheet = new Texture[spritesheet.length];
        for(int i = 0; i < shadowSheet.length; i++){
            shadowSheet[i] = Renderer.toGlassTexture(
                Images.readSpriteSheetToBufferedImage(images.getImage("bee"), glprofile, 8, 3)[i]
            );
        }
    }
    public void draw(GL2 gl){
        double beeAngle = Math.atan2(yVel, xVel) + Math.PI / 2;
        int beeDirection = 7 - ((int)Math.floor((beeAngle + Math.PI/8) / (Math.PI/4)) + 7) % 8;

        if(lastHit + 300 > (int)System.currentTimeMillis()){
            gl.glColor3f(1f, 0.0f, 0.0f);
        } else {
            gl.glColor3f(1f, 1f, 1f);
        }

        Renderer.textureQuad(
            gl, spritesheet[beeDirection * 3 + (Math.abs((int)System.currentTimeMillis() + animationTimeOffset) / 30 % 3)], 
            new float[] {(float)xPos - 0.1f,        1.1f,   (float)yPos - 0.48f}, 
            new float[] {(float)xPos + 1.1f, 1.1f,   (float)yPos - 0.48f},
            new float[] {(float)xPos + 1.1f, 0f, (float)yPos},
            new float[] {(float)xPos - 0.1f,        0f, (float)yPos}
        );
        
        Renderer.textureQuad(
            gl, shadowSheet[beeDirection * 3 + (Math.abs((int)System.currentTimeMillis() + animationTimeOffset) / 30 % 3)],
            new float[] {(float)xPos - 0.1f, 0f, (float)yPos - 0.2f}, 
            new float[] {(float)xPos + 1.1f, 0f, (float)yPos - 0.2f},
            new float[] {(float)xPos + 1.1f, 0f, (float)yPos + 1f},
            new float[] {(float)xPos - 0.1f, 0f, (float)yPos + 1f}
        );
        // reset tint back to normal
        gl.glColor3f(1f, 1f, 1f);
    }
}
