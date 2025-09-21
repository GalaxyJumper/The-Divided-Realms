import java.awt.Frame;
import java.awt.GraphicsConfiguration;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

public class BasicFrame implements GLEventListener{
    public static void main(String args[]){

        final GLProfile profile = GLProfile.get(GLProfile.GL3bc);
        GLCapabilities cap = new GLCapabilities(profile);

        final GLCanvas glcanvas = new GLCanvas(cap);

        BasicFrame b = new BasicFrame();
        final Frame frame = new Frame("Basic Frame");

        glcanvas.addGLEventListener(b);
        glcanvas.setSize(400, 400);

        frame.add(glcanvas);

        frame.setSize(400 , 400);
        frame.setVisible(true);
    }
    @Override
    public void display(GLAutoDrawable arg0) {

    }

    @Override
    public void dispose(GLAutoDrawable arg0) {

    }

    @Override
    public void init(GLAutoDrawable arg0) {

    }

    @Override
    public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4) {

    }
   


}
