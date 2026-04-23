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

import javax.imageio.ImageIO;
public class Renderer {
    
    static GLProfile glprofile = GLProfile.getDefault();

    static Images images = new Images("img");

    static GLCapabilities glcapabilities = new GLCapabilities( glprofile );
    static final GLCanvas glcanvas = new GLCanvas( glcapabilities );
    static final Frame frame = new Frame( "Divided Realms Re-attempt" );

    static Texture bgTexture= null;
    static Texture[][] mapTextures = null; 
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

        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_NEAREST);

        
		gl2.glViewport(0, 0, width, height);

		gl2.glMatrixMode(GL2.GL_PROJECTION);

		gl2.glLoadIdentity();

		glu.gluPerspective(45.0f, (float)width / (float) height, 1.0, 400.0);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

        gl2.setSwapInterval(1); // set to 0 to remove fps cap (turn off VSync)
        
        try {
            playerIdle = Images.readSpriteSheet(images.getImage("player1Idle"), glprofile, 2, 2);
            bgTexture = Renderer.getTextureFromFile(new File("img/Imagation.png"));
            mapTextures = new Texture[Map.currentMap().getHeightChunks()][Map.currentMap().getWidthChunks()];
            for(int i = 0; i < mapTextures.length; i++){
                for(int k = 0; k < mapTextures[i].length; k++){
                    mapTextures[i][k] = Renderer.getChunkTexture(Map.currentMap().getChunk(k, i).getData());
                }   
            }

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    protected static void render( GL2 gl2, int width, int height ) {
        frame.requestFocusInWindow();
        final GL2 gl = gl2;
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	    gl.glLoadIdentity();                
        
        
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, bgTexture.getTextureObject());

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
        
        for(int i = 0; i < Map.currentMap().getWidthChunks(); i ++){
            for(int k = 0; k < Map.currentMap().getHeightChunks(); k++){

                gl2.glBindTexture(GL2.GL_TEXTURE_2D, mapTextures[k][i].getTextureObject());
                TextureCoords texcoords = mapTextures[k][i].getImageTexCoords();

                gl.glLoadIdentity();        

                gl.glTranslatef(-(float) Camera.getX(),-20f + (float)Camera.getY() * (float)Math.sin(Math.PI/4), -(float)Camera.getY() * (float)Math.cos(Math.PI/4) + 7f); 
                gl.glRotatef(45f, 1.0f, 0f, 0f);

                gl.glBegin(GL2.GL_QUADS);               
                
                    gl2.glTexCoord2f(texcoords.right(), texcoords.top());
                    gl.glVertex3f(10f + 10f*(float)i, 0f, 0.0f + 10f*(float)k);
                    
                    
                    gl2.glTexCoord2f(texcoords.left(), texcoords.top());
                    gl.glVertex3f( 0.0f + 10f*(float)i, 0f, 0.0f + 10f*(float)k); 


                    gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
                    gl.glVertex3f( 0.0f + 10f*(float)i, 0f, 10f + 10f*(float)k);     
                    
                    
                    gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
                    gl.glVertex3f(10f + 10f*(float)i, 0f, 10f + 10f*(float)k);   
                    
                    
                gl.glEnd();                            
                gl.glFlush();
            }
        }

        gl.glLoadIdentity();

        gl2.glBindTexture(GL2.GL_TEXTURE_2D, images.getTexture("cliffgrass").getTextureObject());
        TextureCoords texcoords4 = images.getTexture("cliffgrass").getImageTexCoords();

        gl.glTranslatef(-(float) Camera.getX(),-20f + (float)Camera.getY() * (float)Math.sin(Math.PI/4), -(float)Camera.getY() * (float)Math.cos(Math.PI/4) + 7f); 
                gl.glRotatef(45f, 1.0f, 0f, 0f);

                gl.glBegin(GL2.GL_QUADS);               
                
                    gl2.glTexCoord2f(texcoords4.right(), texcoords4.top());
                    gl.glVertex3f(3f, 1f, 3f);
                    
                    
                    gl2.glTexCoord2f(texcoords4.left(), texcoords4.top());
                    gl.glVertex3f(2f, 1f, 2f);


                    gl2.glTexCoord2f(texcoords4.left(), texcoords4.bottom());
                    gl.glVertex3f(2f, 0f, 2f);
                    
                    
                    gl2.glTexCoord2f(texcoords4.right(), texcoords4.bottom());
                    gl.glVertex3f(3f, 0f, 3f);
                    
                    
                gl.glEnd();                            
                gl.glFlush();

                gl2.glBindTexture(GL2.GL_TEXTURE_2D, images.getTexture("grass").getTextureObject());
                texcoords4 = images.getTexture("grass").getImageTexCoords();

                gl.glBegin(GL2.GL_TRIANGLES);               
                
                    gl2.glTexCoord2f(texcoords4.right(), texcoords4.top());
                    gl.glVertex3f(3f, 1f, 2f);
                    
                    
                    gl2.glTexCoord2f(texcoords4.left(), texcoords4.top());
                    gl.glVertex3f(2f, 1f, 2f);

                    
                    gl2.glTexCoord2f(texcoords4.right(), texcoords4.bottom());
                    gl.glVertex3f(3f, 1f, 3f);
                    
                    
                gl.glEnd();                            
                gl.glFlush();


                for(float x = 0; x < 3; x++){
                    gl2.glBindTexture(GL2.GL_TEXTURE_2D, images.getTexture("grass").getTextureObject());
                    texcoords4 = images.getTexture("grass").getImageTexCoords();
                    // TOP
                    gl.glBegin(GL2.GL_QUADS);               
                    
                        gl2.glTexCoord2f(texcoords4.right(), texcoords4.top());
                        gl.glVertex3f(4f + 1f * x, 1f, 2f);
                        
                        
                        gl2.glTexCoord2f(texcoords4.left(), texcoords4.top());
                        gl.glVertex3f(3f + 1f * x, 1f, 2f);


                        gl2.glTexCoord2f(texcoords4.left(), texcoords4.bottom());
                        gl.glVertex3f(3f + 1f * x, 1f, 3f);
                        
                        
                        gl2.glTexCoord2f(texcoords4.right(), texcoords4.bottom());
                        gl.glVertex3f(4f + 1f * x, 1f, 3f);
                        
                        
                    gl.glEnd();                            
                    gl.glFlush();

                    gl2.glBindTexture(GL2.GL_TEXTURE_2D, images.getTexture("cliffgrass").getTextureObject());
                    texcoords4 = images.getTexture("cliffgrass").getImageTexCoords();

                    // Side
                    gl.glBegin(GL2.GL_QUADS);               
                    
                        gl2.glTexCoord2f(texcoords4.right(), texcoords4.top());
                        gl.glVertex3f(4f + 1f * x, 1f, 3f);
                        
                        
                        gl2.glTexCoord2f(texcoords4.left(), texcoords4.top());
                        gl.glVertex3f(3f + 1f * x, 1f, 3f);


                        gl2.glTexCoord2f(texcoords4.left(), texcoords4.bottom());
                        gl.glVertex3f(3f + 1f * x, 0f, 3f);
                        
                        
                        gl2.glTexCoord2f(texcoords4.right(), texcoords4.bottom());
                        gl.glVertex3f(4f + 1f * x, 0f, 3f);
                        
                        
                    gl.glEnd();                            
                    gl.glFlush();
                    
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
            gl.glVertex3f((float)Player.getxPos(),        1f,   (float)Player.getyPos() - 0.35f);
            
            
            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos() + 1f, 1f,   (float)Player.getyPos() - 0.35f); 


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
    public static Texture getChunkTexture(String[][] chunk){
        BufferedImage result = new BufferedImage(240, 240, BufferedImage.TYPE_INT_ARGB);
        Graphics graphics = result.createGraphics();
        for(int x = 0; x < 240; x += 24){
            for(int y = 0; y < 240; y += 24){
                graphics.drawImage((Image)images.getImage(chunk[y / 24][x / 24]), x, y, frame);
            }
        }
        return AWTTextureIO.newTexture(glprofile, result, false);
    }
}