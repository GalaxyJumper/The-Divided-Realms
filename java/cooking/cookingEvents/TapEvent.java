package cooking.cookingEvents;

import java.awt.event.KeyEvent;

public class TapEvent extends CookingEvent{

    private int tapCount = 0;
    private int reqTaps;
    private int keyCode;
                                     
    private final int[] validKeyCodes = {KeyEvent.VK_Z, KeyEvent.VK_X, KeyEvent.VK_C, KeyEvent.VK_V};

    public TapEvent(int numTaps){
        keyCode = validKeyCodes[(int)(Math.random() * 3)];
        reqTaps = (int)(Math.random() * 5);
        System.out.println("Tap time! key code " + keyCode + " and num taps " + reqTaps);
    }
    @Override
    public void update() {
        
    }

    @Override
    public boolean isFinished() {
        return tapCount >= reqTaps;
    }

    @Override
    public double getFinishedQuality() {
        return (double)tapCount / (double) reqTaps;
    }

    
}
