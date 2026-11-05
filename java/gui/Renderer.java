package gui;
import com.jogamp.common.nio.Buffers;
import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
import com.jogamp.nativewindow.util.Point;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import cooking.CookingPot;
import cooking.CookingSystem;
import cooking.ItemData;
import enemies.Bee;
import enemies.Enemy;
import enemies.Slime;
import game.Chunk;
import game.FastNoise;
import game.GameLoop;
import game.Map;
import game.Player;
import gui.Animation;
import gui.Camera;
import gui.Images;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.desktop.SystemEventListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
public class Renderer {
    final static float SQRT_2 = (float)Math.sqrt(2);
    static GLProfile glprofile = GLProfile.getDefault();
    static GLCapabilities glcapabilities = new GLCapabilities(glprofile);
    static final GLCanvas glcanvas = new GLCanvas(glcapabilities);
    private static GLU glu;

    static Images images;

    static FastNoise noise = new FastNoise(5);

    static double startTime = (double)System.currentTimeMillis();

    static final Frame frame = new Frame( "Divided Realms Re-attempt" );

    static Texture bgTexture= null;
    static Texture[][][] mapTextures = null; 
    static Texture[] playerIdle = null;
    static Texture shadowSquare = null;

    static Animation[] dashAnimations;
    static Animation blockAnimation;
    static Animation attackAnimation;
    static Animation spAttackAnimation;

    static boolean texturesLoadad = false;

    static int[] viewportSize = new int[4];

    static final Dimension RESOLUTION = new Dimension(2160, 1440);

    public static void init(){
        glcanvas.setPreferredSize(RESOLUTION);
        glcanvas.addGLEventListener( new GLEventListener() {
            
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                Renderer.setup(glautodrawable.getGL().getGL2(), width, height);
                if(!texturesLoadad) Renderer.loadTextures(width, height);
            }
            
            @Override
            public void init( GLAutoDrawable glautodrawable ) {

            }
            
            @Override
            public void dispose( GLAutoDrawable glautodrawable ) {

            }
            
            @Override
            public void display( GLAutoDrawable glautodrawable ) {
                Renderer.render( glautodrawable.getGL().getGL2(), glautodrawable.getSurfaceWidth(), glautodrawable.getSurfaceHeight() );
            }
        });

        
        frame.add( glcanvas );        
        frame.pack();
        frame.setVisible( true );
        frame.addKeyListener(GameLoop.input);
        frame.addWindowListener(new WindowListener() {
            
            @Override 
            public void windowClosing(java.awt.event.WindowEvent e){
                System.exit(0);
            }

            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub
            }

            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowDeactivated(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowDeiconified(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowIconified(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                // TODO Auto-generated method stub
                
            }
        });
        glcanvas.addMouseListener(GameLoop.input);
        glcanvas.setFocusable(true);
        glcanvas.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
        frame.setFocusable(true);

        glcanvas.requestFocusInWindow();
        System.out.println("[Renderer] Completed init");
    }

    protected static void setup( GL2 gl2, int width, int height ) {
        //gl2.glMatrixMode( GL2.GL_PROJECTION );

        // coordinate system origin at lower left with width and height same as the window
        glu = new GLU();
        // Enable all them textures
        gl2.glEnable(GL2.GL_TEXTURE_2D);
        gl2.glEnable(GL2.GL_BLEND);
        // Set the blend function for transparency
        gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
        
        int side = (int)(width * (9.0 / 16.0));

        gl2.glViewport(0, (height / 2) - (side / 2), width, side);
        if(!texturesLoadad){
            gl2.glMatrixMode(GL2.GL_PROJECTION);
            glu.gluPerspective(45.0f, (float)width / (float) height, 1.0, 400.0);
            gl2.glMatrixMode(GL2.GL_MODELVIEW);
            gl2.glLoadIdentity();

            gl2.setSwapInterval(1); // set to 0 to remove fps cap (turn off VSync)
        }
    
        // Get viewport size
        gl2.glGetIntegerv(GL2.GL_VIEWPORT, viewportSize, 0);

    }
    protected static void loadTextures(int width, int height){
        images = new Images("img", glprofile);
        for(int i = 0; i < GameLoop.getEnemies().size(); i++){
            GameLoop.getEnemies().get(i).loadTextures(glprofile);
        }

        shadowSquare = AWTTextureIO.newTexture(glprofile, toGlass(images.getImage("grass")), false);
        try {
            playerIdle = Images.readSpriteSheet(images.getImage("player1Idle"), glprofile, 2, 2);
            bgTexture = Renderer.getTextureFromFile(new File("img/Imagation.png"));
            mapTextures = new Texture[Map.currentMap().getHeightChunks()][Map.currentMap().getWidthChunks()][Map.HEIGHT];
            
            for(int y = 0; y < mapTextures.length; y++){
                for(int x = 0; x < mapTextures[y].length; x++){
                    for(int h = 0; h < Map.HEIGHT; h++){
                        mapTextures[y][x][h] = Renderer.getChunkTextures(Map.currentMap().getChunk(x, y))[h];
                    }
                }   
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }
        dashAnimations = new Animation[]{
            new Animation(images.getImage("playerQuickDash").getSubimage(0, 0, 96, 24), 4, 1, 4, 100, false),
            
            new Animation(images.getImage("playerQuickDash").getSubimage(0, 24, 96, 24), 4, 1, 4, 100, false),
            
            new Animation(images.getImage("playerQuickDash").getSubimage(0, 48, 96, 24), 4, 1, 4, 100, false),
            
            new Animation(images.getImage("playerQuickDash").getSubimage(0, 72, 96, 24), 4, 1, 4, 100, false)
        };
        blockAnimation = new Animation(images.getImage("blockSheet"), 4, 4, 16, 25, false);
        attackAnimation = new Animation(images.getImage("slash"), 1, 4, 4, 60, false);
        spAttackAnimation = new Animation(images.getImage("slash2"), 2, 3, 6, 83, false);
        texturesLoadad = true;
    }

    protected static void render( GL2 gl2, int width, int height ) {
        
        frame.requestFocusInWindow();

        textureQuad2D(gl2, bgTexture, 0, Renderer.getHeight(), Renderer.getWidth(), -Renderer.getHeight());


        for(int h = 0; h < Map.HEIGHT; h++){
            renderGroundLayer(gl2, h);
        }

        for(int i = 0; i < GameLoop.getEnemies().size(); i++){
            GameLoop.getEnemies().get(i).draw(gl2);
        }

        renderPlayer(gl2);
        // Big tree --------------------------------
        Renderer.textureQuad(gl2, 
            images.getTexture("sdf"),
            new float[] {14.0f, 0f, 16.5f}, //br
            new float[] {10.5f, 0f, 16.5f}, //bl
            new float[] {9.8f, 0f, 10.8f}, //tl
            new float[] {13.25f, 0f, 11f} // tr
        );

        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("image (13)").getSubimage(0, 0, 36, 72), false),
            new float[] {11.5f, 4.24264f, 11.5f},
            new float[] {10f, 4.24264f, 10f},
            new float[] {10f, 0f, 10f},
            new float[] {11.5f, 0f, 11.5f}
        );

        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("image (13)"), false),
            new float[] {13f, 4.24264f, 10f},
            new float[] {10f, 4.24264f, 13f},
            new float[] {10f, 0f, 13f},
            new float[] {13f, 0f, 10f}
        );        
        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("image (13)").getSubimage(36, 0, 36, 72), false),
            new float[] {13f, 4.24264f, 13f},
            new float[] {11.5f, 4.24264f, 11.5f},
            new float[] {11.5f, 0f, 11.5f},
            new float[] {13f, 0f, 13f}
        );
        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("image (18)"), false),
            new float[] {13f, 2.8f, 10.6f},
            new float[] {10f, 2.8f, 10.6f},
            new float[] {10f, 0.6f, 12.6f},
            new float[] {13f, 0.6f, 12.6f}
        ); 

        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("image (22)"), false),
            new float[] {12.8f, 3.5f, 10.6f},
            new float[] {10.2f, 3.5f, 10.6f},
            new float[] {10.2f, 1.3f, 12.2f},
            new float[] {12.8f, 1.3f, 12.2f}
        ); 
        // End tree --------------------------------

        renderCookingPot(gl2, CookingSystem.getCookingPot());

        // double[] hitbox = Player.getAttackHitbox();
        // Renderer.textureQuad(gl2, 
        //     AWTTextureIO.newTexture(glprofile, images.getImage("Square1"), false),
        //     new float[] {(float)hitbox[0], 0.01f, (float)hitbox[1]},
        //     new float[] {(float)(hitbox[0] + hitbox[2]), 0.01f, (float)hitbox[1]},
        //     new float[] {(float)(hitbox[0] + hitbox[2]), 0.01f, (float)(hitbox[1] + hitbox[3])},
        //     new float[] {(float)hitbox[0], 0.01f, (float)(hitbox[1] + hitbox[3])}
        // );

        // Inventory UI

        double slotSize = 100.0 * ((double)viewportSize[2] / 1080.0);
        double barStartX = (viewportSize[2] / 2) - slotSize * 5;
        for(int i = 0; i < 10; i++){
            String imageName = (String) ItemData.getItemProperty(Player.getInventorySlot(i), "image");
            if(imageName == null){
                imageName = "Square1";
            }
            textureQuad2D(gl2, images.getTexture("Square"), barStartX + slotSize * i, viewportSize[3] - slotSize * 2, slotSize, slotSize);
            textureQuad2D(gl2, images.getTexture(imageName), (barStartX + 0.1 * slotSize) + (slotSize * i), viewportSize[3] - 1.8 * slotSize, 0.8 * slotSize, 0.8 * slotSize);
        }
        
        Renderer.textureQuad2D(gl2, images.getTexture("tentaclys"), viewportSize[2] - 220, 200, 220, 660);
        String[][] toolSlots = CookingSystem.getToolSlots();
        toolSlots[0][1] = "slimebeesoup";
        toolSlots[0][2] = "honey";
        toolSlots[0][3] = "toastedbeewings";

        
        toolSlots[1][1] = "slimebeesoup";
        toolSlots[1][2] = "honey";
        toolSlots[1][3] = "beewing";

        
        toolSlots[2][1] = "slicedslime";
        toolSlots[2][2] = "slimesandwich";
        toolSlots[2][3] = "honey";

        for(int i = 0; i < toolSlots.length; i++){
            for(int k = 0; k < toolSlots[i].length; k++){
                if(toolSlots[i][k] != null)
                Renderer.textureQuad2D(gl2, images.getTexture(toolSlots[i][k]), viewportSize[2] - 220 + (int)(Math.sin(k + i) * 20), 240 + 220 * i + (int)(Math.cos(i + k) * 5), 80, 80);
            }
        }
        CookingSystem.updateRequiringGL(gl2);
        Player.updateRequiringGL(gl2);
    }
    
    public static void renderPlayer(GL2 gl2){

        int imageNumIDK = 0;
        

        
        if(Player.getyDir() < 0){
            imageNumIDK += 2;
        }
        if(Player.getxDir() > 0){
            imageNumIDK += 1;
        }

        gl2.glBindTexture(GL2.GL_TEXTURE_2D, playerIdle[imageNumIDK].getTextureObject());

        // DASH ANIMATION
        if(Player.getState() == Player.PlayerState.DASHING){
            dashAnimations[imageNumIDK].setStartTime(Player.getLastDash());
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, AWTTextureIO.newTexture(glprofile, dashAnimations[imageNumIDK].getFrame(), false).getTextureObject());
        }        
        // ATTACK ANIMATION
        if((int)System.currentTimeMillis() - Player.getLastAttack() < 200){
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, AWTTextureIO.newTexture(glprofile, dashAnimations[imageNumIDK].getFrame(), false).getTextureObject());
        }
        // DRAW PLAYER QUAD
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        // Render player
        TextureCoords texcoords = playerIdle[imageNumIDK].getImageTexCoords();
        int mapheight = Map.currentMap().getHeight((int)(Player.getxPos() + 0.5), (int)(Player.getyPos() + 0.5)) + 
                        Map.currentMap().getCliffHeight((int)(Player.getxPos() + 0.5), (int)(Player.getyPos() + 0.5));
        gl2.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl2.glVertex3f((float)Player.getxPos(),        1f + mapheight + (float)(0.1 * (double)Math.cos(System.currentTimeMillis() / 350.0)),   (float)Player.getyPos() - 0.38f);
            
            
            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl2.glVertex3f((float)Player.getxPos() + 1f, 1f + mapheight + (float)(0.1 * (double)Math.cos(System.currentTimeMillis() / 350.0)),   (float)Player.getyPos() - 0.38f); 


            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl2.glVertex3f((float)Player.getxPos() + 1f, 0f + mapheight + (float)(0.1 * (double)Math.cos(System.currentTimeMillis() / 350.0)), (float)Player.getyPos());     
            
            
            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl2.glVertex3f((float)Player.getxPos(),        0f + mapheight + (float)(0.1 * (double)Math.cos(System.currentTimeMillis() / 350.0)), (float)Player.getyPos());   
            
            
        gl2.glEnd();                            
        gl2.glFlush();

        // Draw shadow
        gl2.glBindTexture(
            GL2.GL_TEXTURE_2D, 
            AWTTextureIO.newTexture(
                glprofile, 
                toGlass(
                    Images.readSpriteSheetToBufferedImage(
                        images.getImage("player1Idle"), 
                        glprofile, 
                        2, 
                        2
                    )[imageNumIDK]
                ), 
                false
            ).getTextureObject()
        );
        // Draw shadow quad
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        // Render player
        gl2.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl2.glVertex3f((float)Player.getxPos(),        0f + mapheight,   (float)Player.getyPos() + 1f);
            
            
            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl2.glVertex3f((float)Player.getxPos() + 1f, 0f + mapheight,   (float)Player.getyPos() + 1f); 


            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl2.glVertex3f((float)Player.getxPos() + 1f, 0 + mapheight, (float)Player.getyPos());     
            
            
            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl2.glVertex3f((float)Player.getxPos(),        0 + mapheight, (float)Player.getyPos());   
            
            
        gl2.glEnd();                            
        gl2.glFlush();
        // Draw shield
        if(Player.getState() == Player.PlayerState.BLOCKING){
            blockAnimation.setStartTime(Player.getLastBlock());
            Renderer.textureQuad(
                gl2, AWTTextureIO.newTexture(glprofile, blockAnimation.getFrame(), false),
                new float[]{(float)Player.getxPos(),        1f + mapheight,   (float)Player.getyPos() - 0.38f},
                new float[]{(float)Player.getxPos() + 1f, 1f + mapheight,   (float)Player.getyPos() - 0.38f}, 
                new float[]{(float)Player.getxPos() + 1f, 0f + mapheight, (float)Player.getyPos()},
                new float[]{(float)Player.getxPos(),        0f + mapheight, (float)Player.getyPos()}
            );
        }
        // Determine regular attack hitbox and special attack hitbox
        if(Player.getState() == Player.PlayerState.ATTACKING || Player.getLastAttack() + 200 > (int)System.currentTimeMillis()){
            double[] hitboxCenter = new double[]{
                Player.getAttackHitbox()[0] + 1,
                Player.getAttackHitbox()[1] + 1
            };
            double[] playerCenter = new double[]{
                Player.getxPos() + 0.5,
                Player.getyPos() + 0.5
            };
            double magnitude = Math.sqrt(
                Math.pow(( playerCenter[0] - hitboxCenter[0]),2)  +  Math.pow((playerCenter[1] - hitboxCenter[1]),2)
            );
            double[] playerToAttackHitboxVector = new double[] {
                (playerCenter[0] - hitboxCenter[0]) / magnitude,
                (playerCenter[1] - hitboxCenter[1]) / magnitude
            }; 
            double[] normalToTheRight = new double[] {
                -playerToAttackHitboxVector[1] * 1.2,
                playerToAttackHitboxVector[0] * 1.2
            };
            double[] normalToTheLeft = new double[] {
                playerToAttackHitboxVector[1] * 1.2,
                -playerToAttackHitboxVector[0] * 1.2
            };
            float[] tr = new float[]{
                (float)(playerCenter[0]  - 1.7 * playerToAttackHitboxVector[0] + normalToTheRight[0]),
                (float)(playerCenter[1]  - 1.7 * playerToAttackHitboxVector[1] + normalToTheRight[1] - 1)
            };
            float[] tl = new float[]{
                (float)(playerCenter[0]  - 1.7 * playerToAttackHitboxVector[0]  + normalToTheLeft[0]),
                (float)(playerCenter[1]  - 1.7 * playerToAttackHitboxVector[1]  + normalToTheLeft[1] - 1)
            };
            float [] br = new float[]{
                (float)(playerCenter[0] - 0.3 * playerToAttackHitboxVector[0] + normalToTheRight[0]),
                (float)(playerCenter[1]   - 0.3 * playerToAttackHitboxVector[1] + normalToTheRight[1] - 1)
            };
            
            float [] bl = new float[]{
                (float)(playerCenter[0]  - 0.3 * playerToAttackHitboxVector[0] + normalToTheLeft[0]),
                (float)(playerCenter[1]  - 0.3 * playerToAttackHitboxVector[1] + normalToTheLeft[1] - 1)
            };
            attackAnimation.setStartTime(Player.getLastAttack());
            // Draw attack slash animation
            Renderer.textureQuad(
                //order for texture quad is tr, tl, bl, br
                gl2, AWTTextureIO.newTexture(glprofile, attackAnimation.getFrame(), false),
                new float[]{tr[0],        0.0f + mapheight,   tr[1]},
                new float[]{tl[0], 0.0f + mapheight,   tl[1]}, 
                new float[]{bl[0], 0.0f + mapheight, bl[1]},
                new float[]{br[0],        0.0f + mapheight, br[1]}
            );
        }
        // Draw special attack animation
        if(Player.getState() == Player.PlayerState.SPECIAL_ATTACKING){
            spAttackAnimation.setStartTime(Player.getLastSpecialAttack());
            Renderer.textureQuad(
                gl2, AWTTextureIO.newTexture(glprofile, spAttackAnimation.getFrame(), false),
                new float[]{(float)Player.getxPos() - 1f,        1f + mapheight,   (float)Player.getyPos() - 0.5f},
                new float[]{(float)Player.getxPos() + 2f, 1f + mapheight,   (float)Player.getyPos() - 0.5f}, 
                new float[]{(float)Player.getxPos() +2f, 0f + mapheight, (float)Player.getyPos() +1.5f},
                new float[]{(float)Player.getxPos() - 1f,        0f + mapheight, (float)Player.getyPos() + 1.5f}
            );
        }
    }

    public static void renderUI(GL2 gl2){

    }

    public static void renderCookingPot(GL2 gl2, CookingPot pot){
        float cookingPotX = pot.getXPos();
        float cookingPotY = pot.getYPos();
        Renderer.textureQuad(gl2, 
            images.getTexture("potshadow"),
            new float[] {23.75f, 0f, 14.2f}, //br
            new float[] {20.25f, 0f, 14.2f}, //bl
            new float[] {19.55f, 0f, 8.5f}, //tl
            new float[] {23f, 0f, 8.7f} // tr
        );
        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("potbasebig").getSubimage(0, 0, 36, 72), false),
            new float[] {21.5f, 4.24264f, 11.5f},
            new float[] {20f, 4.24264f, 10f},
            new float[] {20f, 0f, 10f},
            new float[] {21.5f, 0f, 11.5f}
        );

        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("potbasebig"), false),
            new float[] {23f, 4.24264f, 10f},
            new float[] {20f, 4.24264f, 13f},
            new float[] {20f, 0f, 13f},
            new float[] {23f, 0f, 10f}
        );        
        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("potbasebig").getSubimage(36, 0, 36, 72), false),
            new float[] {23f, 4.24264f, 13f},
            new float[] {21.5f, 4.24264f, 11.5f},
            new float[] {21.5f, 0f, 11.5f},
            new float[] {23f, 0f, 13f}
        );        
        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("pottop"), false),
            new float[] {22.25f, 1.70f, 10.8f},
            new float[] {20.75f, 1.70f, 10.8f},
            new float[] {20.75f, 0.55f, 12f},
            new float[] {22.25f, 0.55f, 12f}
        );        
        Renderer.textureQuad(gl2, 
            AWTTextureIO.newTexture(glprofile, images.getImage("pothole"), false),
            new float[] {22.25f, 1.70f, 10.8f},
            new float[] {20.75f, 1.70f, 10.8f},
            new float[] {20.75f, 0.85f, 12f},
            new float[] {22.25f, 0.85f, 12f}
        );
        for(int i = 0; i < pot.getIngredients().size(); i++){
            float margin = (1f - ((18f/22f))) / 2f ;
            Renderer.textureQuad(gl2, 
                images.getTexture("cookingslotend"),
                new float[] {cookingPotX - 1, 3 + GameLoop.ONE_OVER_SQRT2, cookingPotY - 0.5f - GameLoop.ONE_OVER_SQRT2},
                new float[] {cookingPotX - 2, 3 + GameLoop.ONE_OVER_SQRT2, cookingPotY - 0.5f - GameLoop.ONE_OVER_SQRT2},
                new float[] {cookingPotX - 2, 3                          , cookingPotY - 0.5f                          },
                new float[] {cookingPotX - 1, 3                          , cookingPotY - 0.5f                          }
            );
            Renderer.textureQuad(gl2, 
                images.getTexture("cookingslot"),
                new float[] {cookingPotX - 1, 3 - GameLoop.ONE_OVER_SQRT2 * i      , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * i},
                new float[] {cookingPotX - 2, 3 - GameLoop.ONE_OVER_SQRT2 * i      , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * i},
                new float[] {cookingPotX - 2, 3 - GameLoop.ONE_OVER_SQRT2 * (i + 1), cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + 1)},
                new float[] {cookingPotX - 1, 3 - GameLoop.ONE_OVER_SQRT2 * (i + 1), cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + 1)}
            );
            Renderer.textureQuad(gl2, 
                AWTTextureIO.newTexture(glprofile, images.getImage((String)ItemData.getItemProperty(CookingSystem.getCookingPot().getIngredients().get(i), "image")), false),
                new float[] {cookingPotX - 1 - margin, 3 - GameLoop.ONE_OVER_SQRT2 * (i + margin)      , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + margin)},
                new float[] {cookingPotX - 2 + margin, 3 - GameLoop.ONE_OVER_SQRT2 * (i + margin)      , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + margin)},
                new float[] {cookingPotX - 2 + margin, 3 - GameLoop.ONE_OVER_SQRT2 * (i + 1 - margin), cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + 1 - margin)},
                new float[] {cookingPotX - 1 - margin, 3 - GameLoop.ONE_OVER_SQRT2 * (i + 1 - margin), cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + 1 - margin)}
            );
            if(i == CookingSystem.getCookingPot().getIngredients().size() - 1){
                Renderer.textureQuad(gl2, 
                    images.getTexture("cookingslotend"),
                    new float[] {cookingPotX - 1, 3 - GameLoop.ONE_OVER_SQRT2 * (i + 2)      , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + 2)},
                    new float[] {cookingPotX - 2, 3 - GameLoop.ONE_OVER_SQRT2 * (i + 2)      , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + 2)},
                    new float[] {cookingPotX - 2, 3 - GameLoop.ONE_OVER_SQRT2 * (i + 1)      , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + 1)},
                    new float[] {cookingPotX - 1, 3 - GameLoop.ONE_OVER_SQRT2 * (i + 1)      , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (i + 1)}
                );
            }
        }
        if(pot.getState() == CookingPot.State.COOKING){
                float cookingCompletion = 1f - (float)((int)System.currentTimeMillis() - pot.getCurrentDish().getTimeStarted()) / (float)pot.getCurrentDish().getCookTime();
                Renderer.textureQuad(gl2, 
                    images.getTexture("meadow"),
                    new float[] {cookingPotX - 1, 3 - GameLoop.ONE_OVER_SQRT2 * (3) * cookingCompletion, cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (3) * cookingCompletion},
                    new float[] {cookingPotX - 2, 3 - GameLoop.ONE_OVER_SQRT2 * (3) * cookingCompletion, cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * (3) * cookingCompletion},
                    new float[] {cookingPotX - 2, 3 - GameLoop.ONE_OVER_SQRT2 * 3                , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * 3},
                    new float[] {cookingPotX - 1, 3 - GameLoop.ONE_OVER_SQRT2 * 3                , cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * 3}
                );
                Renderer.textureQuad(gl2, 
                    images.getTexture("Square"),
                    new float[] {cookingPotX - 1, 3, cookingPotY - 0.5f},
                    new float[] {cookingPotX - 2, 3, cookingPotY - 0.5f},
                    new float[] {cookingPotX - 2, 3 - GameLoop.ONE_OVER_SQRT2 * 3, cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * 3},
                    new float[] {cookingPotX - 1, 3 - GameLoop.ONE_OVER_SQRT2 * 3, cookingPotY - 0.5f + GameLoop.ONE_OVER_SQRT2 * 3}
                );
        }
    }

    public static void renderEnvironment(){
        
    }
    public static void renderGroundLayer(GL2 gl, int layer){
        float sysTime = (float)( ((double) System.currentTimeMillis() - startTime) / 1000.0);
        
        for(int y = 0; y < Map.currentMap().getHeightChunks(); y++){
            for(int x = 0; x < Map.currentMap().getWidthChunks(); x ++){
        
                /////////// RENDER GROUND /////////
                
                // Render ground layer for this level. If it's null then this layer is empty and we don't need to draw it
                if(mapTextures[y][x][layer] != null){
                    Renderer.textureQuad(gl, mapTextures[y][x][layer], 
                        new float[] {10f + 10f*(float)x, 0f + layer, 0.0f + 10f*(float)y}, 
                        new float[] {0.0f + 10f*(float)x, 0f + layer, 0.0f + 10f*(float)y},
                        new float[] {0.0f + 10f*(float)x, 0f + layer, 10f + 10f*(float)y}, 
                        new float[] {10f + 10f*(float)x, 0f + layer, 10f + 10f*(float)y}
                    );
                } 
                /* WATER STUFF ======================================
                float a = 10;
                float b = 10;
                float noiseValue = noise.GetNoise(24 * (a + 1 + sysTime),  24 * (b));
                Texture texture = 
                    toWater(
                        images.getImage(
                            "water " + (Math.round(Math.abs(noise.GetNoise(20 * a, 20 * b))) + 1)
                        ), 
                        noiseValue
                    );
                Renderer.textureQuad(gl, texture, 
                    new float[] {1f + 1f*(float)a, -1f + layer + 0.3f * noiseValue,      1f*(float)b}, 
                    new float[] {     1f*(float)a, -1f + layer + 0.3f * noiseValue,      1f*(float)b},
                    new float[] {     1f*(float)a, -1f + layer + 0.3f * noiseValue, 1f + 1f*(float)b}, 
                    new float[] {1f + 1f*(float)a, -1f + layer + 0.3f * noiseValue, 1f + 1f*(float)b}
                );
                THIS CODE IS VERY MUCH ALIVE I AM PUTTING IT IN CRYO FOR NOW
                */ 
                Chunk currentChunk = Map.currentMap().getChunk(x, y);
                // Render cliff sides
                for(int y2 = 0; y2 < 10; y2++){
                    for(int x2 = 0; x2 < 10; x2++){
                        if(layer >= currentChunk.getHeightAt(x2, y2) && layer < currentChunk.getHeightAt(x2, y2) + currentChunk.getCliffHeightAt(x2, y2)){
                            boolean noCliffs = false;
                            String cliffData = currentChunk.getCliffDirAt(x2, y2);
                            int startXoffset = 0;
                            int startYoffset = 0;
                            int endXoffset = 0;
                            int endYoffset = 0;
                            
                            if(cliffData.equals("n")){
                                startXoffset = 0;
                                startYoffset = 0;
                                endXoffset = 1;
                                endYoffset = 0;
                            }
                            else if(cliffData.equals("e")){
                                startXoffset = 1;
                                startYoffset = 0;
                                endXoffset = 1;
                                endYoffset = 1;

                            }
                            else if(cliffData.equals("s")){
                                startXoffset = 0;
                                startYoffset = 1;
                                endXoffset = 1;
                                endYoffset = 1;

                            }
                            else if(cliffData.equals("w")){
                                startXoffset = 0;
                                startYoffset = 0;
                                endXoffset = 0;
                                endYoffset = 1;

                            }

                            else if(cliffData.equals("ne")){
                                startXoffset = 0;
                                startYoffset = 0;
                                endXoffset = 1;
                                endYoffset = 1;
                                if(layer == currentChunk.getHeightAt(x2, y2))
                                Renderer.textureQuad(gl, images.getTexture("grass_clifftop_BL"), 
                                    new float[] {1f + 1f*(float)x2, 0f + layer, 0.0f + 1f*(float)y2}, 
                                    new float[] {0.0f + 1f*(float)x2, 0f + layer, 0.0f + 1f*(float)y2},
                                    new float[] {0.0f + 1f*(float)x2, 0f + layer, 1f + 1f*(float)y2}, 
                                    new float[] {1f + 1f*(float)x2, 0f + layer, 1f + 1f*(float)y2}
                                );

                            }
                            else if( cliffData.equals("sw")){
                                startXoffset = 0;
                                startYoffset = 0;
                                endXoffset = 1;
                                endYoffset = 1;
                                if(layer == currentChunk.getHeightAt(x2, y2))
                                Renderer.textureQuad(gl, images.getTexture("grass_clifftop_TR"), 
                                    new float[] {1f + 1f*(float)x2, 0f + layer, 0.0f + 1f*(float)y2}, 
                                    new float[] {0.0f + 1f*(float)x2, 0f + layer, 0.0f + 1f*(float)y2},
                                    new float[] {0.0f + 1f*(float)x2, 0f + layer, 1f + 1f*(float)y2}, 
                                    new float[] {1f + 1f*(float)x2, 0f + layer, 1f + 1f*(float)y2}
                                );                                
                            }
                            else if(cliffData.equals("nw")){
                                startXoffset = 1;
                                startYoffset = 0;
                                endXoffset = 0;
                                endYoffset = 1;
                                if(layer == currentChunk.getHeightAt(x2, y2))
                                Renderer.textureQuad(gl, images.getTexture("grass_clifftop_BR"), 
                                    new float[] {1f + 1f*(float)x2, 0f + layer, 0.0f + 1f*(float)y2}, 
                                    new float[] {0.0f + 1f*(float)x2, 0f + layer, 0.0f + 1f*(float)y2},
                                    new float[] {0.0f + 1f*(float)x2, 0f + layer, 1f + 1f*(float)y2}, 
                                    new float[] {1f + 1f*(float)x2, 0f + layer, 1f + 1f*(float)y2}
                                );

                            } 
                            else if(cliffData.equals("se")){
                                startXoffset = 1;
                                startYoffset = 0;
                                endXoffset = 0;
                                endYoffset = 1;
                                if(layer == currentChunk.getHeightAt(x2, y2))
                                Renderer.textureQuad(gl, images.getTexture("grass_clifftop_TL"), 
                                    new float[] {1f + 1f*(float)x2, 0f + layer, 0.0f + 1f*(float)y2}, 
                                    new float[] {0.0f + 1f*(float)x2, 0f + layer, 0.0f + 1f*(float)y2},
                                    new float[] {0.0f + 1f*(float)x2, 0f + layer, 1f + 1f*(float)y2}, 
                                    new float[] {1f + 1f*(float)x2, 0f + layer, 1f + 1f*(float)y2}
                                );
                            }
                            else {
                                noCliffs = true;
                            }
                            if(!noCliffs){
                                int xTiles = currentChunk.getXChunks() * 10 + x2;
                                int yTiles = currentChunk.getYChunks() * 10 + y2;
                                    Texture cliffTexture;
                                    TextureCoords cliffTextureCoords;
                                    // this isn't the top layer, so it doesn't need grass
                                    if (layer != currentChunk.getCliffHeightAt(x2, y2) + currentChunk.getHeightAt(x2, y2) - 1){
                                        cliffTexture = images.getTexture("cliffside_dark");
                                        cliffTextureCoords = cliffTexture.getImageTexCoords();
                                    } 
                                    // this is the top layer so it needs grass
                                    else {                                    
                                        cliffTexture = images.getTexture("cliffgrass_dark");
                                        cliffTextureCoords = cliffTexture.getImageTexCoords();
                                    }
                                    
                                    Renderer.textureQuad(
                                        gl, 
                                        cliffTexture, 
                                        new float[] {xTiles + endXoffset, layer + 1f, yTiles + endYoffset}, 
                                        new float[] { xTiles + startXoffset, layer + 1f, yTiles + startYoffset}, 
                                        new float[] { xTiles + startXoffset, layer, yTiles + startYoffset}, 
                                        new float[] { xTiles + endXoffset, layer, yTiles + endYoffset}
                                    );

                                    if(layer == currentChunk.getHeightAt(x2, y2)){
                                        Renderer.textureQuad(
                                            gl, 
                                            images.getTexture("cliffshadow (2)"), 
                                            new float[] {xTiles + endXoffset, layer + 1.2f, yTiles + endYoffset}, 
                                            new float[] { xTiles + startXoffset, layer + 1.2f, yTiles + startYoffset}, 
                                            new float[] { xTiles + startXoffset, layer, yTiles + startYoffset}, 
                                            new float[] { xTiles + endXoffset, layer, yTiles + endYoffset}
                                        );
                                        
                                        Renderer.textureQuad(
                                            gl, 
                                            images.getTexture("cliffshadow (2)"), 
                                            new float[] {xTiles + endXoffset, layer, yTiles + endYoffset + 1f}, 
                                            new float[] { xTiles + startXoffset, layer, yTiles + startYoffset + 1f}, 
                                            new float[] { xTiles + startXoffset, layer, yTiles + startYoffset}, 
                                            new float[] { xTiles + endXoffset, layer, yTiles + endYoffset}
                                        );

                                        Renderer.textureQuad(
                                            gl, 
                                            shadowSquare, 
                                            new float[] {xTiles + endXoffset + 0.2f, layer, yTiles + endYoffset + 1f}, 
                                            new float[] { xTiles + startXoffset + 0.2f, layer, yTiles + startYoffset + 1f}, 
                                            new float[] { xTiles + startXoffset, layer, yTiles + startYoffset}, 
                                            new float[] { xTiles + endXoffset, layer, yTiles + endYoffset}
                                        );
                                    }
                                

                            }
                        }
                    }
                }

            }
        }
    }
    public static void renderEnemy(Enemy e, GL2 gl){

    }
    public static void renderCliffs(GL2 gl){


                
                
    }
    public static void textureQuad(GL2 gl, Texture texture, float[] tr, float[] tl, float[] bl, float[] br){
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject());
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);

        TextureCoords texcoords = texture.getImageTexCoords();

        gl.glLoadIdentity();        

        gl.glTranslatef(-(float) Camera.getX(),-20f + (float)Camera.getY() * (float)Math.sin(Math.PI/4), -(float)Camera.getY() * (float)Math.cos(Math.PI/4) + 7f); 
        gl.glRotatef(45f, 1.0f, 0f, 0f);

        

        FloatBuffer vertices  = Buffers.newDirectFloatBuffer(
            new float[]{bl[0], bl[1], bl[2], // bottom left corner
                      tl[0],  tl[1], tl[2], // top left corner
                       tr[0],  tr[1], tr[2], // top right corner
                       br[0], br[1], br[2] // bottom right corner
                    }
        );
        FloatBuffer texVerts  = Buffers.newDirectFloatBuffer(
            new float[]{texcoords.left(), texcoords.bottom(), // bottom left corner
                      texcoords.left(),  texcoords.top(), // top left corner
                       texcoords.right(),  texcoords.top(), // top right corner
                       texcoords.right(), texcoords.bottom() // bottom right corner
                    }
        );

        ByteBuffer indices = Buffers.newDirectByteBuffer(
            new byte[]{0,1,2, // first triangle (bottom left - top left - top right)
                       0,2,3}
        ); // second triangle (bottom left - top right - bottom right)

        gl.glVertexPointer(3, GL2.GL_FLOAT, 0, vertices);
        gl.glTexCoordPointer(2, GL2.GL_FLOAT, 0, texVerts);

        gl.glDrawElements(GL2.GL_TRIANGLES, 6, GL2.GL_UNSIGNED_BYTE, indices);

        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
    }
    
    public static void renderGame(){
        glcanvas.display();
    }
    public static double[] worldToScreen(double x, double y, double z, GL2 gl){
             
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();   
        gl.glTranslatef(-(float) Camera.getX(), -20f + (float)Camera.getY() * (float)Math.sin(Math.PI/4), -(float)Camera.getY() * (float)Math.cos(Math.PI/4) + 7f); 
        gl.glRotatef(45f, 1.0f, 0f, 0f);
        

        double[] model = new double[16];
        double[] proj = new double[16];
        int[] viewport = new int[4];
        double[] results = new double[3];
        gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, model, 0);
        gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, proj, 0);
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);
        glu.gluProject(
            x, y, z,
            model, 0,
            proj, 0,
            viewport, 0,
            results, 0
        );
        gl.glPopMatrix();
        // Result y= screen height - y         (invert result y)
        results[1] = viewport[3] - results[1];
        return results;
    }
    public static void textureQuad2D(
        GL2 gl,
        Texture texture,
        double x,
        double y,
        double width,
        double height) {

        int[] viewport = new int[4];
        gl.glGetIntegerv(GL2.GL_VIEWPORT, viewport, 0);

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glOrtho(
            0,
            viewport[2],
            viewport[3],
            0,
            -1,
            1
        );

        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPushMatrix();
        gl.glLoadIdentity();

        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject());
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        gl.glColor4f(1f, 1f, 1f, 1f);

        gl.glBegin(GL2.GL_QUADS);
            gl.glTexCoord2f(0, 0);
            gl.glVertex2d(x, y);

            gl.glTexCoord2f(1, 0);
            gl.glVertex2d(x + width, y);

            gl.glTexCoord2f(1, 1);
            gl.glVertex2d(x + width, y + height);

            gl.glTexCoord2f(0, 1);
            gl.glVertex2d(x, y + height);
        gl.glEnd();

        gl.glPopMatrix();

        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();

        gl.glMatrixMode(GL2.GL_MODELVIEW);
    }

    public static Texture getTextureFromFile(File f) throws IOException{
        return AWTTextureIO.newTexture(f, true);
    }
    public static BufferedImage toGlass(BufferedImage image){
        
        // Color of the final shadow (usually black)
        Color color = new Color(0, 0, 0);
        // Result image
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);

        // Copy the alpha channel from the original image
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null);

        // Set the composite rule to only affect non-transparent pixels
        /* @see https://ssp.impulsetrain.com/porterduff.html */
        g.setComposite(AlphaComposite.SrcIn.derive(0.3f));

        // Set the desired color and fill the entire image
        g.setColor(color);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        return result;
    }
    public static Texture toWater(BufferedImage image, float transparency){
        transparency = (float)GameLoop.clamp(transparency*transparency, 0.0f, 0.7f);
        // Color of the final shadow (usually black)
        Color color = new Color(0, 0, 0);
        // Result image
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);

        // Copy the alpha channel from the original image
        Graphics2D g = result.createGraphics();
        
        // Set the composite rule to only affect non-transparent pixels
        /* @see https://ssp.impulsetrain.com/porterduff.html */
        g.setComposite(AlphaComposite.SrcOver.derive(0.9f));

        g.drawImage(image, 0, 0, null);

        
        // Set the composite rule to only affect non-transparent pixels
        /* @see https://ssp.impulsetrain.com/porterduff.html */
        g.setComposite(AlphaComposite.SrcOver.derive(transparency));

        g.setColor(Color.WHITE);

        g.fillRect(0, 0, image.getWidth(), image.getHeight());

        return AWTTextureIO.newTexture(glprofile, result, false);
    }
    public static Texture toGlassTexture(BufferedImage image){
        
        // Color of the final shadow (usually black)
        Color color = new Color(0, 0, 0);
        // Result image
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);

        // Copy the alpha channel from the original image
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null);

        // Set the composite rule to only affect non-transparent pixels
        /* @see https://ssp.impulsetrain.com/porterduff.html */
        g.setComposite(AlphaComposite.SrcIn.derive(0.3f));

        // Set the desired color and fill the entire image
        g.setColor(color);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        return AWTTextureIO.newTexture(glprofile, result, false);
    }
    public static BufferedImage toGlassLess(BufferedImage image){
        
        // Color of the final shadow (usually black)
        Color color = new Color(0, 0, 0);
        // Result image
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), Transparency.BITMASK);

        // Copy the alpha channel from the original image
        Graphics2D g = result.createGraphics();
        g.drawImage(image, 0, 0, null);

        // Set the composite rule to only affect non-transparent pixels
        /* @see https://ssp.impulsetrain.com/porterduff.html */
        g.setComposite(AlphaComposite.SrcIn.derive(0.2f));

        // Set the desired color and fill the entire image
        g.setColor(color);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        return result;
    }
    public static Texture[] getChunkTextures(Chunk chunk){
        BufferedImage[] result = new BufferedImage[Map.HEIGHT];
        Graphics[] graphics = new Graphics[Map.HEIGHT];
        boolean[] isLayerEmpty = new boolean[Map.HEIGHT];
        for(int i = 0; i < isLayerEmpty.length; i++) isLayerEmpty[i] = true;

        for(int i = 0; i < Map.HEIGHT; i++){
            result[i] = new BufferedImage(240, 240, BufferedImage.BITMASK);
            graphics[i] = result[i].createGraphics();
        }

        for(int x = 0; x < 240; x += 24){
            for(int y = 0; y < 240; y += 24){
                int mapHeight = chunk.getHeightAt(x / 24, y / 24) + chunk.getCliffHeightAt(x / 24, y / 24); 
                Image tileImage = (Image)images.getImage(chunk.getTileAt(x / 24, y / 24));
                graphics[mapHeight].drawImage(
                    tileImage,
                    x,
                    y, 
                    null
                );
                isLayerEmpty[mapHeight] = false;
            }
        }
        Texture[] textures = new Texture[graphics.length];
        for(int i = 0 ; i < result.length; i++){
            textures[i] = AWTTextureIO.newTexture(glprofile, result[i], false);
            if(isLayerEmpty[i]) textures[i] = null;
        }
        return textures;
    }
    public static int getWidth(){
        return Renderer.getViewportSize()[2];
    }
    
    public static int getHeight(){
        return Renderer.getViewportSize()[3];
    }
    public static Dimension getResolution(){
        return new Dimension(frame.getWidth(), frame.getHeight());
    }
    public static int[] getViewportSize(){
        return viewportSize;
    }
    public static GLProfile getGLProfile() {
        if (glprofile == null) {
            glprofile = GLProfile.getDefault();
        }
        return glprofile;
    }
    
}