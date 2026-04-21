import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowListener;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureCoords;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
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

    static Texture imageTexture = null;
    static Texture image2Texture= null;
    static Texture image3Texture= null;
    static Texture[] playerIdle = null;
    static BufferedImage[] player1Idle = null;

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

        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
        gl2.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);

        
		gl2.glViewport(0, 0, width, height);

		gl2.glMatrixMode(GL2.GL_PROJECTION);

		gl2.glLoadIdentity();

		glu.gluPerspective(45.0f, (float)width / (float) height, 1.0, 20.0);
		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

        gl2.setSwapInterval(0); // set to 0 to remove fps cap (turn off VSync)
        
        try {
            player1Idle = Images.readSpriteSheetToBufferedImage(images.getImage("player1Idle"), glprofile, 2, 2);
            playerIdle = Images.readSpriteSheet(images.getImage("player1Idle"), glprofile, 2, 2);
            imageTexture = Renderer.getTextureFromFile(new File("img/tile_grass.png"));
            image2Texture = playerIdle[2];
            image3Texture = Renderer.getTextureFromFile(new File("img/Imagation.png"));
            

        }
        catch(IOException e){
            e.printStackTrace();
            imageTexture = null;
        }

    }

    protected static void render( GL2 gl2, int width, int height ) {
        frame.requestFocusInWindow();
        final GL2 gl = gl2;
		gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
	    gl.glLoadIdentity();                
        
        
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, image3Texture.getTextureObject());

        TextureCoords texcoords2 = image3Texture.getImageTexCoords();

        gl.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords2.right(), texcoords2.top());
            gl.glVertex3f(2f, 1.125f, -3f);
            
            
            gl2.glTexCoord2f(texcoords2.left(), texcoords2.top());
            gl.glVertex3f(-2f, 1.125f, -3f); 


            gl2.glTexCoord2f(texcoords2.left(), texcoords2.bottom());
            gl.glVertex3f(-2f, -1.125f, -3f);     
            
            
            gl2.glTexCoord2f(texcoords2.right(), texcoords2.bottom());
            gl.glVertex3f(2f, -1.125f, -3f);   
            
            
        gl.glEnd();                            
        gl.glFlush();
			             
        if(imageTexture != null){
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, image3Texture.getTextureObject());
        }

        TextureCoords texcoords = imageTexture.getImageTexCoords();
        for(int i = 0; i < 6; i ++){
            for(int k = 0; k < 6; k++){
                gl.glLoadIdentity();        

                gl.glTranslatef(-(float) Camera.getX(),-2f + (float)Camera.getY() * (float)Math.sin(Math.PI/4), -(float)Camera.getY() * (float)Math.cos(Math.PI/4) + 0.5f); 
                gl.glRotatef(45f, 1.0f, 0f, 0f);

                gl.glBegin(GL2.GL_QUADS);               
                
                    gl2.glTexCoord2f(texcoords.right(), texcoords.top());
                    gl.glVertex3f(0.0f + 1f*(float)i, 0f, 0.0f + 1f*(float)k);
                    
                    
                    gl2.glTexCoord2f(texcoords.left(), texcoords.top());
                    gl.glVertex3f( 1f + 1f*(float)i, 0f, 0.0f + 1f*(float)k); 


                    gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
                    gl.glVertex3f( 1f + 1f*(float)i, 0f, 1f + 1f*(float)k);     
                    
                    
                    gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
                    gl.glVertex3f(0.0f + 1f*(float)i, 0f, 1f + 1f*(float)k);   
                    
                    
                gl.glEnd();                            
                gl.glFlush();
            }
        }

        int imageNumIDK = 0;

        
        if(Player.getyDir() < 0){
            imageNumIDK += 2;
        }
        if(Player.getxDir() > 0){
            imageNumIDK += 1;
        }

        image2Texture = playerIdle[imageNumIDK];

        gl2.glBindTexture(GL2.GL_TEXTURE_2D, image2Texture.getTextureObject());

        // Render player
        // TODO: Meshing logic: Get the texture of a chunk and draw that instead
        // For cliffs: each tile is 1 wide by like 3 tall and is an image of the cliff
        // Get the chunk images on world load.
        // For animations: do a check (isAnimated) and create a texture for each frame of the animation.

        gl.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos(),        0f,   (float)Player.getyPos());
            
            
            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos() + 0.1f, 0f,   (float)Player.getyPos()); 


            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl.glVertex3f((float)Player.getxPos() + 0.1f, 0.1f, (float)Player.getyPos() - 0.03f);     
            
            
            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl.glVertex3f((float)Player.getxPos(),        0.1f, (float)Player.getyPos() - 0.03f);   
            
            
        gl.glEnd();                            
        gl.glFlush();


        gl2.glBindTexture(GL2.GL_TEXTURE_2D, AWTTextureIO.newTexture(glprofile, toGlass(player1Idle[imageNumIDK]), false).getTextureObject());

        // Render player
        gl.glBegin(GL2.GL_QUADS);               
        
            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos(),        0f,   (float)Player.getyPos());
            
            
            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl.glVertex3f((float)Player.getxPos() + 0.1f, 0f,   (float)Player.getyPos()); 


            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl.glVertex3f((float)Player.getxPos() + 0.1f, 0, (float)Player.getyPos() + 0.1f);     
            
            
            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl.glVertex3f((float)Player.getxPos(),        0, (float)Player.getyPos() + 0.1f);   
            
            
        gl.glEnd();                            
        gl.glFlush();

    }



    public static void renderImageTexture(GL2 gl2, Texture imageTex, int x, int y, int width, int height){
        if(imageTexture != null){
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, imageTex.getTextureObject());
        }


        TextureCoords texcoords = imageTex.getImageTexCoords();
        gl2.glTranslatef(x, y, 0);

        gl2.glBegin(GL2.GL_QUADS);
            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl2.glVertex3f(0, 0, 0f);

            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl2.glVertex3f(width, 0, 0f);

            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl2.glVertex3f(width, height, 0f);

            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl2.glVertex2f(0, height);
        gl2.glEnd();
        gl2.glFlush();

        gl2.glTranslatef(-x, -y, 0);
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }
    public static void renderImageTexture(GL2 gl2, Texture imageTex, int x, int y){
                if(imageTexture != null){
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, imageTex.getTextureObject());
        }
        TextureCoords texcoords = imageTex.getImageTexCoords();
        gl2.glTranslatef(x, y, 0);

        gl2.glBegin(GL2.GL_QUADS);

            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl2.glVertex2f(0, 0);

            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl2.glVertex2f(imageTex.getWidth(), 0);

            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl2.glVertex2f(imageTex.getWidth(), imageTex.getHeight());

            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl2.glVertex2f(0, imageTex.getHeight());
            
        gl2.glEnd();
        gl2.glFlush();

        gl2.glTranslatef(-x, -y, 0);
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }
    public static void renderImageTesture(GL2 gl2, Texture imageTex, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
        if(imageTexture != null){
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, imageTex.getTextureObject());
        }
        TextureCoords texcoords = imageTex.getImageTexCoords();

        gl2.glBegin(GL2.GL_QUADS);

            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl2.glVertex2f(x1, y1);

            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl2.glVertex2f(x2, y2);

            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl2.glVertex2f(x3, y3);

            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl2.glVertex2f(x4, y4);
            
        gl2.glEnd();
        gl2.glFlush();
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
    }
    public static void renderImageQuad(GL2 gl2, Texture imageTex, int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4){
        double a1 = y3 - y1;
        double b1 = x1 - x3;
        double c1 = a1*(x1) + b1*(y1);
     
        // Line CD represented as a2x + b2y = c2
        double a2 = y4 - y2;
        double b2 = x2 - x4;
        double c2 = a2*(x2)+ b2*(y2);
     
        double determinant = a1*b2 - a2*b1;

        
        int quadCenterX1 = (int)((x1 + x2 + x3 + x4) / 4);
        int quadCenterY1 = (int)((y1 + y2 + y3 + y4) / 4);
        
        int quadCenterX2 = (int)((b2*c1 - b1*c2)/determinant);
        int quadCenterY2 = (int)((a1*c2 - a2*c1)/determinant);

        int quadCenterX = quadCenterX2;
        int quadCenterY = quadCenterY2;

        if(imageTexture != null){
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, imageTex.getTextureObject());
        }
        TextureCoords texcoords = imageTex.getImageTexCoords();

        // LEFT
        gl2.glBegin(GL2.GL_TRIANGLES);

            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl2.glVertex2f(x1, y1);

            gl2.glTexCoord2f(texcoords.right() / 2, texcoords.top() / 2);
            gl2.glVertex2f(quadCenterX, quadCenterY);

            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl2.glVertex2f(x4, y4);

            
        gl2.glEnd();

        // BOTTOM
        gl2.glBegin(GL2.GL_TRIANGLES);
        
            gl2.glTexCoord2f(texcoords.left(), texcoords.bottom());
            gl2.glVertex2f(x1, y1);

            
            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl2.glVertex2f(x2, y2);

            gl2.glTexCoord2f(texcoords.right() / 2, texcoords.top() / 2);
            gl2.glVertex2f(quadCenterX, quadCenterY);
        
            
        gl2.glEnd();

        // RIGHT
        gl2.glBegin(GL2.GL_TRIANGLES);         

            gl2.glTexCoord2f(texcoords.right(), texcoords.bottom());
            gl2.glVertex2f(x2, y2);

            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl2.glVertex2f(x3, y3);

            gl2.glTexCoord2f(texcoords.right() / 2, texcoords.top() / 2);
            gl2.glVertex2f(quadCenterX, quadCenterY);

        gl2.glEnd();

        // TOP
        gl2.glBegin(GL2.GL_TRIANGLES);

            gl2.glTexCoord2f(texcoords.left(), texcoords.top());
            gl2.glVertex2f(x4, y4);

            gl2.glTexCoord2f(texcoords.right() / 2, texcoords.top() / 2);
            gl2.glVertex2f(quadCenterX, quadCenterY);

            gl2.glTexCoord2f(texcoords.right(), texcoords.top());
            gl2.glVertex2f(x3, y3);
        
        gl2.glEnd();



        gl2.glFlush();

        gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);


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

}