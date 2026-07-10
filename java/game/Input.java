package game;
import java.awt.event.*;

public class Input implements KeyListener, MouseListener{
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

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        GameLoop.handleMouseClick(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

}
