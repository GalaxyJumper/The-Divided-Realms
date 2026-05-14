import java.awt.image.BufferedImage;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Slime extends Enemy{
    private double[] pathfindingTarget = new double[2];
    private int lastTargetUpdate = (int) System.currentTimeMillis() - 5000;
    private int timeTilNextTargetUpdate = 4500;
    private Texture[] spritesheet = Images.readSpriteSheet(images.getImage("slime"), GLProfile.getDefault(), 2, 4);
    private Texture[] shadowSheet = new Texture[spritesheet.length];
    private int animationTimeOffset = (int)(Math.random() * 1500.0);
    public Slime(double x, double y){
        super(x, y);
        for(int i = 0; i < shadowSheet.length; i++){
            shadowSheet[i] = Renderer.toGlassTexture(
                Images.readSpriteSheetToBufferedImage(images.getImage("slime"), GLProfile.getDefault(), 2, 4)[i]
            );
        }
    }

    @Override
    public void update() {
        xPos += xVel;
        yPos += yVel;
        if((int) System.currentTimeMillis() - lastTargetUpdate > timeTilNextTargetUpdate){
            lastTargetUpdate = (int) System.currentTimeMillis();
            timeTilNextTargetUpdate = (int)(Math.abs(Math.random()) * 10000.0 + 2000.0);
            pathfindingTarget = new double[] {
                xPos + Math.random() * 6 - 3,
                yPos + Math.random() * 6 - 3
            };
        }
        xVel = GameLoop.clamp((pathfindingTarget[0] - xPos) * 0.5, -0.06, 0.06);
        yVel = GameLoop.clamp((pathfindingTarget[1] - yPos) * 0.5, -0.06, 0.06);
        pathfindingTarget[0] += (Math.random() * 0.024) - 0.012;
        pathfindingTarget[1] += (Math.random() * 0.024) - 0.012;
        if(Math.sqrt(Math.pow(xPos - Player.getxPos(), 2) + Math.pow(yPos - Player.getyPos(), 2)) < 4){
            pathfindingTarget[0] = Player.getxPos();
            pathfindingTarget[1] = Player.getyPos();
        }

    }
    public void draw(GL2 gl){
        Renderer.texturedQuad(
            gl, spritesheet[Math.abs((int)System.currentTimeMillis() + animationTimeOffset) / 150 % 7], 
            new float[] {(float)xPos,        1f,   (float)yPos - 0.38f}, 
            new float[] {(float)xPos + 1f, 1f,   (float)yPos - 0.38f},
            new float[] {(float)xPos + 1f, 0f, (float)yPos},
            new float[] {(float)xPos,        0f, (float)yPos}
        );
        
        Renderer.texturedQuad(
            gl, shadowSheet[Math.abs((int)System.currentTimeMillis() + animationTimeOffset) / 150 % 7],
            new float[] {(float)xPos + 1f,        0f,   (float)yPos + 1f}, 
            new float[] {(float)xPos, 0f,   (float)yPos + 1f},
            new float[] {(float)xPos, 0f, (float)yPos},
            new float[] {(float)xPos + 1f,        0f, (float)yPos}
        );
    }
}
