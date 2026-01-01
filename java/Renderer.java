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
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;


import java.awt.Frame;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
public class Renderer {
    
    static GLProfile glprofile = GLProfile.getDefault();

    
    static GLCapabilities glcapabilities = new GLCapabilities( glprofile );
    static final GLCanvas glcanvas = new GLCanvas( glcapabilities );

    static Texture imageTexture = null;

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

        final Frame frame = new Frame( "One Triangle AWT" );
        frame.add( glcanvas );
        // frame.addWindowListener( new WindowAdapter() {
        //     public void windowClosing( WindowEvent windowevent ) {
        //         frame.remove( glcanvas );
        //         frame.dispose();
        //         System.exit( 0 );
        //     }
        // });

        frame.setSize( 1280, 720 );
        frame.setVisible( true );
    }

    protected static void setup( GL2 gl2, int width, int height ) {
        gl2.glMatrixMode( GL2.GL_PROJECTION );

        // coordinate system origin at lower left with width and height same as the window
        GLU glu = new GLU();
        glu.gluOrtho2D( 0.0f, width, 0.0f, height );

        gl2.glMatrixMode( GL2.GL_MODELVIEW );
        gl2.glLoadIdentity();

        gl2.glEnable(GL2.GL_TEXTURE_2D);
        
        gl2.glViewport( 0, 0, width, height );

        
        try {
            imageTexture = Renderer.getTextureFromFile(new File("C:\\Users\\Default Unburnt\\Java Projects\\The-Divided-Realms\\The-Divided-Realms\\img\\Pathway to Eternity.png"));
        }
        catch(IOException e){
            e.printStackTrace();
            imageTexture = null;
        }

    }

    protected static void render( GL2 gl2, int width, int height ) {
        gl2.glClear(GL2.GL_COLOR_BUFFER_BIT);
        int x = 0;
        int y = 0;
        int imgWidth = imageTexture.getWidth();
        int imgHeight = imageTexture.getHeight();
        
        if(imageTexture != null){
            gl2.glBindTexture(GL2.GL_TEXTURE_2D, imageTexture.getTextureObject());
        }
        //gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        //gl2.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        gl2.glTranslatef(300, 300, 0);

        gl2.glBegin(GL2.GL_QUADS);
            gl2.glTexCoord2f(0, 0);
            gl2.glVertex2f(-imgWidth / 2, -imgHeight / 2);

            gl2.glTexCoord2f(1, 0);
            gl2.glVertex2f(imgWidth / 2, -imgHeight / 2);

            gl2.glTexCoord2f(1, 1);
            gl2.glVertex2f(imgWidth / 2, imgHeight / 2);

            gl2.glTexCoord2f(0, 1);
            gl2.glVertex2f(-imgWidth / 2, imgHeight / 2);
        gl2.glEnd();
        gl2.glFlush();

        gl2.glTranslatef(-300, -300, 0);
        gl2.glBindTexture(GL2.GL_TEXTURE_2D, 0);
        //gl.glRotatef


    }

    public static void renderGame(){
        glcanvas.display();
    }

    public static Texture getTextureFromFile(File f) throws IOException{
        return AWTTextureIO.newTexture(f, true);
    }
}