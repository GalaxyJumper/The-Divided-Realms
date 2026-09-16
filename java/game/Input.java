package game;
import java.awt.Component;
import java.awt.event.*;

import gui.Renderer;
import maptool.MapTool;

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
        double mouseX = e.getX() * ((double)Renderer.getViewportSize()[2] / Renderer.getWidth());
        double mouseY = e.getY() * ((double)Renderer.getViewportSize()[3] / Renderer.getHeight());
        e = new MouseEvent((Component)e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), (int)mouseX, (int)mouseY, e.getClickCount(), e.isPopupTrigger());

        System.out.println("Mouse pressed at: " + e.getX() + ", " + e.getY());
        if(GameLoop.mapToolMode){
            System.out.println("Mouse pressed at: " + e.getX() + ", " + e.getY());
            MapTool.handleMouseClick(e);
        }
        else{
            GameLoop.handleMouseClick(e);
        }
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
