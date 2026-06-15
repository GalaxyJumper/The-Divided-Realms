import java.awt.event.*;

public class Input implements KeyListener{
    private static boolean[] keys;

    public Input(){
        keys = new boolean[120];
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() < 120){
            keys[e.getKeyCode()] = true;
        } 
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() < 120){
            keys[e.getKeyCode()] = false;
        }
    }

    public boolean getKey(int keyCode){
        return keys[keyCode];
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }

}
