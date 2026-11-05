package game;
import java.awt.Component;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
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
        Player.handleKeyPress(e);
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
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(GameLoop.mapToolMode){
            System.out.println("Mouse pressed at: " + e.getX() + ", " + e.getY());
            MapTool.handleMouseClick(e);
        }
        else {
            int[] viewport = Renderer.getViewportSize();
            double mouseX = e.getX() * ((double)viewport[2] / Renderer.getResolution().getWidth()) + viewport[0];
            double mouseY = e.getY() * ((double)viewport[3] / Renderer.getResolution().getHeight()) + viewport[1];
        
            System.out.println("Mouse pressed at: " + e.getX() + ", " + e.getY());
            System.out.printf("Frame height: %f | Actual height: %d\n", Renderer.getResolution().getHeight(), Renderer.getViewportSize()[3]);

            e = new MouseEvent((Component)e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(), (int)mouseX, (int)mouseY, e.getClickCount(), e.isPopupTrigger());
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
    public static int getMouseX(){
        int[] viewport = Renderer.getViewportSize();
        double rawX = MouseInfo.getPointerInfo().getLocation().getX();
        return (int)(rawX * ((double)Renderer.getViewportSize()[2] / (double)Renderer.getResolution().getWidth())) + viewport[0];
    }
    public static int getMouseY(){
        int[] viewport = Renderer.getViewportSize();
        double rawY = MouseInfo.getPointerInfo().getLocation().getY();
        return (int)(rawY * ((double)Renderer.getViewportSize()[3] / (double)Renderer.getResolution().getHeight())) + viewport[1];  
    }
    public static boolean mouseInRect(Rectangle r){
        return r.contains(new Point(getMouseX(), getMouseY()));
    }
}
