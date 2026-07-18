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

        xPos += xVel; 
        yPos += yVel;

        yVel *= 0.95;
        xVel *= 0.95;

        hitbox = new double[]{xPos, yPos, 1, 1};

        double distanceToTarget = GameLoop.dist(xPos, yPos, pathfindingTarget[0], pathfindingTarget[1]);
        distanceToTarget = Math.min(2, distanceToTarget);
        // Set velocity as a vector pointing in the direction of the target
        yVel += 0.001 * (pathfindingTarget[1] - yPos) * distanceToTarget;
        xVel += 0.001 * (pathfindingTarget[0] - xPos) * distanceToTarget;

        int currentFrame = (int)Math.floor(Math.abs(now + animationTimeOffset) / 100 % 7);

        // On frame 1 the slime is touching the ground and so that is when we update the target pose. 
        // Also slow down for that slime jump effect.

        if(currentFrame >= 1 && currentFrame <= 2){
            xVel *= 0.25;
            yVel *= 0.25;
            if(GameLoop.dist(xPos, yPos, Player.getxPos(), Player.getyPos()) < 0.5) lastAggro = now;
            if(now - lastAggro < aggroTime && GameLoop.dist(xPos, yPos, Player.getxPos(), Player.getyPos()) < 9){
                pathfindingTarget[0] = Player.getxPos();
                pathfindingTarget[1] = Player.getyPos();
            } else {
                pathfindingTarget[0] = xPos + Math.random() * 10 - 5;
                pathfindingTarget[1] = yPos + Math.random() * 10 - 5;
            }
        }
        if(this.health <= 0){ // die
            GameLoop.getEnemies().remove(this);
        }
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
        Renderer.textureQuad(   
            gl, spritesheet[Math.abs((int)System.currentTimeMillis() + animationTimeOffset) / 100 % 7], 
            new float[] {(float)xPos,        1f,   (float)yPos - 0.38f}, 
            new float[] {(float)xPos + 1f, 1f,   (float)yPos - 0.38f},
            new float[] {(float)xPos + 1f, 0f, (float)yPos},
            new float[] {(float)xPos,        0f, (float)yPos}
        );
        
        Renderer.textureQuad(
            gl, shadowSheet[Math.abs((int)System.currentTimeMillis() + animationTimeOffset) / 100 % 7],
            new float[] {(float)xPos + 1f,        0f,   (float)yPos + 1f}, 
            new float[] {(float)xPos, 0f,   (float)yPos + 1f},
            new float[] {(float)xPos, 0f, (float)yPos},
            new float[] {(float)xPos + 1f,        0f, (float)yPos}
        );
    }
}
