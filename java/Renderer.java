import com.jogamp.common.nio.Buffers;
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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
public class Renderer {
    
    static GLProfile glprofile = GLProfile.getDefault();

    static Images images;

    static Bee[] boi;

    static Slime[] bois;

    static FastNoise noise = new FastNoise(5);

    static double startTime = (double)System.currentTimeMillis();

    static GLCapabilities glcapabilities = new GLCapabilities( glprofile );
    static final GLCanvas glcanvas = new GLCanvas( glcapabilities );
    static final Frame frame = new Frame( "Divided Realms Re-attempt" );

    static Texture bgTexture= null;
    static Texture[][][] mapTextures = null; 
    static Texture[] playerIdle = null;
    static Texture shadowSquare = null;
    static Animation[] dashAnimations;
    static Animation blockAnimation;
    public static void init(){
        glcanvas.addGLEventListener( new GLEventListener() {
            
            @Override
            public void reshape( GLAutoDrawable glautodrawable, int x, int y, int width, int height ) {
                
                Renderer.setup( glautodrawable.getGL().getGL2(), width, height );
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

        frame.setSize( 1080, 720 );
        frame.setVisible( true );
        frame.addKeyListener(GameLoop.input);
        frame.setFocusable(true);

        frame.requestFocusInWindow();
        System.out.println("[Renderer] Completed init");
    }

    protected static void setup( GL2 gl2, int width, int height ) {
        //gl2.glMatrixMode( GL2.GL_PROJECTION );

        // coordinate system origin at lower left with width and height same as the window
        GLU glu = new GLU();

        images = new Images("img", glprofile);

        boi = new Bee[5];
        bois = new Slime[5];


        // glu.gluOrtho2D( 0.0f, width, 0.0f, height );

        // gl2.glMatrixMode( GL2.GL_MODELVIEW );
        // gl2.glLoadIdentity();

        // Enable all them textures
        gl2.glEnable(GL2.GL_TEXTURE_2D);
        
        gl2.glEnable(GL2.GL_BLEND);

        // Set the blend function for transparency
        gl2.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);


		gl2.glViewport(0, 0, width, height);

		gl2.glMatrixMode(GL2.GL_PROJECTION);

		gl2.glLoadIdentity();

		glu.gluPerspective(45.0f, (float)width / (float) height, 1.0, 400.0);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

        gl2.setSwapInterval(1); // set to 0 to remove fps cap (turn off VSync)
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

        //TODO: move this to game loop
        for(int i = 0; i < boi.length; i++){
            boi[i] = new Bee(6 + Math.random() * 9, 6 + Math.random() *9);
        }
        
        //TODO: move this to game loop
        for(int i = 0; i < bois.length; i++){
            bois[i] = new Slime(6 + Math.random() * 10, 6 + Math.random() *5);
        }


    }

    protected static void render( GL2 gl2, int width, int height ) {
        frame.requestFocusInWindow();

        gl2.glLoadIdentity();                
        
        // Immediate rendering on purpose; I don't feel a need to create a method specifically for 2d quads :p
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, bgTexture.getTextureObject());
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        TextureCoords texcoords2 = bgTexture.getImageTexCoords();

        gl2.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords2.right(), texcoords2.top());
            gl2.glVertex3f(20f, 11.25f, -30f);
            
            
            gl2.glTexCoord2f(texcoords2.left(), texcoords2.top());
            gl2.glVertex3f(-20f, 11.25f, -30f); 


            gl2.glTexCoord2f(texcoords2.left(), texcoords2.bottom());
            gl2.glVertex3f(-20f, -11.25f, -30f);     
            
            
            gl2.glTexCoord2f(texcoords2.right(), texcoords2.bottom());
            gl2.glVertex3f(20f, -11.25f, -30f);   
            
            
        gl2.glEnd();                            
        gl2.glFlush();

        for(int h = 0; h < Map.HEIGHT; h++){
            renderGroundLayer(gl2, h);
        }



        for(int i = 0; i < boi.length; i++){
            boi[i].draw(gl2);
            boi[i].update();
        }
        for(int i = 0; i < bois.length; i++){
            bois[i].draw(gl2);
            bois[i].update();
        }

        renderPlayer(gl2);
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


        if(Player.getState() == Player.PlayerState.DASHING){
            dashAnimations[imageNumIDK].setStartTime(Player.getLastDash());
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, AWTTextureIO.newTexture(glprofile, dashAnimations[imageNumIDK].getFrame(), false).getTextureObject());
        }
        
        if(Player.getState() == Player.PlayerState.ATTACKING){

        }
        
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
                                int yTiles = currentChunk.getXChunks() * 10 + y2;
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
    public static GLProfile getGLProfile(){
        return glprofile;
    }
}