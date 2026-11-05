package cooking.cookingEvents;

import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.util.InputMismatchException;
import java.util.Scanner;

import com.jogamp.opengl.GL2;

import cooking.CookingPot;
import game.Input;
import gui.Renderer;

public class StirEvent extends CookingEvent{
    
    double currentTotalAngle = 0;

    double reqTotalAngle;

    double mouseX = 0;
    double mouseY = 0;
    double pMouseX = 0;
    double pMouseY = 0;

    CookingPot cookingPot;

    double quality = 0;
    boolean complete = false;

    public StirEvent(double reqTotalAngle, CookingPot cookingPot){
        super();
        this.reqTotalAngle = reqTotalAngle;
        this.cookingPot = cookingPot;
    }
    // Runs every frame and calculates currentTotalAngle.
    @Override
    public void update() {
        
        mouseX = Input.getMouseX();
        mouseY = Input.getMouseY();

        int anchorX = cookingPot.getXOnScreen();
        int anchorY = cookingPot.getYOnScreen();


        

        double dTheta = Math.atan2(mouseY - anchorY, mouseX - anchorX) - Math.atan2(pMouseY - anchorY, pMouseX - anchorX);

        if(dTheta > Math.PI){
            dTheta = Math.atan2(mouseY - 200, mouseX - 200) - Math.atan2(pMouseY - 200, pMouseX - 200);
        }
        if(dTheta < -Math.PI){
            dTheta = Math.atan2(mouseY - 200, mouseX - 200) - Math.atan2(pMouseY - 200, pMouseX - 200);
        }

        pMouseX = mouseX;
        pMouseY = mouseY;
        currentTotalAngle += Math.toDegrees(dTheta);

        quality = currentTotalAngle / reqTotalAngle;

        if(Math.abs(currentTotalAngle) > reqTotalAngle){
            complete = true;
        }

    }

    @Override
    public boolean isFinished() {
        return Math.abs(currentTotalAngle) > reqTotalAngle;
    }
    // Returns a double 0 to 1 indicating the amount of stirring completed
    @Override
    public double getFinishedQuality() {
        return quality;
    }
}
