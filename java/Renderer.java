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
public class Renderer {
    
    static GLProfile glprofile = GLProfile.getDefault();

    static Images images = new Images("img");

    static GLCapabilities glcapabilities = new GLCapabilities( glprofile );
    static final GLCanvas glcanvas = new GLCanvas( glcapabilities );
    static final Frame frame = new Frame( "Divided Realms Re-attempt" );

    static Texture bgTexture= null;
    static Texture[][][] mapTextures = null; 
    static Texture[] playerIdle = null;
    static Animation[] dashAnimations = new Animation[]{
        new Animation(images.getImage("playerQuickDash").getSubimage(0, 0, 96, 24), 4, 1, 4, 100, false),
        
        new Animation(images.getImage("playerQuickDash").getSubimage(0, 24, 96, 24), 4, 1, 4, 100, false),
        
        new Animation(images.getImage("playerQuickDash").getSubimage(0, 48, 96, 24), 4, 1, 4, 100, false),
        
        new Animation(images.getImage("playerQuickDash").getSubimage(0, 72, 96, 24), 4, 1, 4, 100, false)
    };

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
        // glu.gluOrtho2D( 0.0f, width, 0.0f, height );

        // gl2.glMatrixMode( GL2.GL_MODELVIEW );
        // gl2.glLoadIdentity();

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

        gl2.setSwapInterval(0); // set to 0 to remove fps cap (turn off VSync)
        
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
            System.out.println("sahgiuyasgfuyasegrkwa");
        }

    }

    protected static void render( GL2 gl2, int width, int height ) {
        frame.requestFocusInWindow();
        final GL2 gl = gl2;
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	    gl.glLoadIdentity();                
        
        
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, bgTexture.getTextureObject());
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        TextureCoords texcoords2 = bgTexture.getImageTexCoords();

        gl.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords2.right(), texcoords2.top());
            gl.glVertex3f(20f, 11.25f, -30f);
            
            
            gl2.glTexCoord2f(texcoords2.left(), texcoords2.top());
            gl.glVertex3f(-20f, 11.25f, -30f); 


            gl2.glTexCoord2f(texcoords2.left(), texcoords2.bottom());
            gl.glVertex3f(-20f, -11.25f, -30f);     
            
            
            gl2.glTexCoord2f(texcoords2.right(), texcoords2.bottom());
            gl.glVertex3f(20f, -11.25f, -30f);   
            
            
        gl.glEnd();                            
        gl.glFlush();
        for(int h = 0; h < Map.HEIGHT; h++){
            renderGroundLayer(gl, h);
        }


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
        
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        if(Player.getState() == Player.PlayerState.ATTACKING){

        }
        if(Player.getState() == Player.PlayerState.BLOCKING){
            
        }

        // Render player
        // TODO: Meshing logic: Get the texture of a chunk and draw that instead
        // For cliffs: each tile is 1 wide by like 3 tall and is an image of the cliff
        // Get the chunk images on world load.
        // For animations: do a check (isAnimated) and create a texture for each frame of the animation.

        TextureCoords texcoords = playerIdle[imageNumIDK].getImageTexCoords();

        gl.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos(),        1f,   (float)Player.getyPos() - 0.38f);
            
            
            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos() + 1f, 1f,   (float)Player.getyPos() - 0.38f); 


            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl.glVertex3f((float)Player.getxPos() + 1f, 0f, (float)Player.getyPos());     
            
            
            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl.glVertex3f((float)Player.getxPos(),        0f, (float)Player.getyPos());   
            
            
        gl.glEnd();                            
        gl.glFlush();


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
        gl.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos(),        0f,   (float)Player.getyPos() + 1f);
            
            
            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos() + 1f, 0f,   (float)Player.getyPos() + 1f); 


            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl.glVertex3f((float)Player.getxPos() + 1f, 0, (float)Player.getyPos());     
            
            
            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl.glVertex3f((float)Player.getxPos(),        0, (float)Player.getyPos());   
            
            
        gl.glEnd();                            
        gl.glFlush();
    }

    public static void renderPlayer(){

    }

    public static void renderEnvironment(){
        
    }
    public static void renderGroundLayer(GL2 gl, int layer){
        for(int y = 0; y < Map.currentMap().getHeightChunks(); y++){
            for(int x = 0; x < Map.currentMap().getWidthChunks(); x ++){
        
                /////////// RENDER GROUND /////////

                Renderer.texturedQuad(gl, mapTextures[y][x][layer], 
                    new float[] {10f + 10f*(float)x, 0f + layer, 0.0f + 10f*(float)y}, 
                    new float[] {0.0f + 10f*(float)x, 0f + layer, 0.0f + 10f*(float)y},
                    new float[] {0.0f + 10f*(float)x, 0f + layer, 10f + 10f*(float)y}, 
                    new float[] {10f + 10f*(float)x, 0f + layer, 10f + 10f*(float)y}
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

                            else if(cliffData.equals("ne") || cliffData.equals("sw")){
                                startXoffset = 0;
                                startYoffset = 0;
                                endXoffset = 1;
                                endYoffset = 1;

                            }
                            else if(cliffData.equals("nw") || cliffData.equals("se")){
                                startXoffset = 1;
                                startYoffset = 0;
                                endXoffset = 0;
                                endYoffset = 1;

                            } else {
                                noCliffs = true;
                            }
                            if(!noCliffs){
                                int xTiles = currentChunk.getXChunks() * 10 + x2;
                                int yTiles = currentChunk.getXChunks() * 10 + y2;
                                    TextureCoords cliffTextureCoords;
                                    if (layer != currentChunk.getCliffHeightAt(x2, y2) - 1){
                                        gl.glBindTexture(GL2.GL_TEXTURE_2D, images.getTexture("cliffside").getTextureObject());
                                        cliffTextureCoords = images.getTexture("cliffside").getImageTexCoords();
                                    } else {                                    
                                        gl.glBindTexture(GL2.GL_TEXTURE_2D, images.getTexture("cliffgrass").getTextureObject());
                                        cliffTextureCoords = images.getTexture("cliffgrass").getImageTexCoords();
                                    }
                                    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
                                    gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

                                    gl.glLoadIdentity();        

                                    gl.glTranslatef(-(float) Camera.getX(),-20f + (float)Camera.getY() * (float)Math.sin(Math.PI/4), -(float)Camera.getY() * (float)Math.cos(Math.PI/4) + 7f); 
                                    gl.glRotatef(45f, 1.0f, 0f, 0f);
                                    gl.glBegin(GL2.GL_QUADS);               
                                    
                                        gl.glTexCoord2f(cliffTextureCoords.right(), cliffTextureCoords.top());
                                        gl.glVertex3f(xTiles + endXoffset, layer + 1f, yTiles + endYoffset);
                                        
                                        
                                        gl.glTexCoord2f(cliffTextureCoords.left(), cliffTextureCoords.top());
                                        gl.glVertex3f( xTiles + startXoffset, layer + 1f, yTiles + startYoffset); 


                                        gl.glTexCoord2f(cliffTextureCoords.left(), cliffTextureCoords.bottom());
                                        gl.glVertex3f( xTiles + startXoffset, layer, yTiles + startYoffset);     
                                        
                                        
                                        gl.glTexCoord2f(cliffTextureCoords.right(), cliffTextureCoords.bottom());
                                        gl.glVertex3f(xTiles + endXoffset, layer, yTiles + endYoffset);   
                                        
                                        
                                    gl.glEnd();                            
                                    gl.glFlush();
                                

                            }
                        }
                    }
                }

            }
        }
    }
    public static void renderCliffs(GL2 gl){


                
                
    }
    public static void texturedQuad(GL2 gl, Texture texture, float[] tr, float[] tl, float[] bl, float[] br){
        gl.glBindTexture(GL2.GL_TEXTURE_2D, texture.getTextureObject());
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);
        TextureCoords texcoords = texture.getImageTexCoords();

        gl.glLoadIdentity();        

        gl.glTranslatef(-(float) Camera.getX(),-20f + (float)Camera.getY() * (float)Math.sin(Math.PI/4), -(float)Camera.getY() * (float)Math.cos(Math.PI/4) + 7f); 
        gl.glRotatef(45f, 1.0f, 0f, 0f);

        gl.glBegin(GL2.GL_QUADS);               
        
            gl.glTexCoord2f(texcoords.right(), texcoords.top());
            gl.glVertex3f(tr[0], tr[1], tr[2]); 
            
            
            gl.glTexCoord2f(texcoords.left(), texcoords.top());
            gl.glVertex3f(tl[0], tl[1], tl[2]); 


            gl.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl.glVertex3f(bl[0], bl[1], bl[2]); 
            
            
            gl.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl.glVertex3f(br[0], br[1], br[2]); 
            
            
        gl.glEnd();                            
        gl.glFlush();
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
    public static Texture[] getChunkTextures(Chunk chunk){
        BufferedImage[] result = new BufferedImage[Map.HEIGHT];
        Graphics[] graphics = new Graphics[Map.HEIGHT];

        for(int i = 0; i < Map.HEIGHT; i++){
            result[i] = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
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
            }
        }
        Texture[] textures = new Texture[graphics.length];
        for(int i = 0 ; i < result.length; i++){
            textures[i] = AWTTextureIO.newTexture(glprofile, result[i], false);
        }
        return textures;
    }
}