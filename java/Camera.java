public class Camera {
    static double x = 6;
    static double y = 20;
    static double offsetX = 0.5;
    static double offsetY = 28;

    static public void update(double playerX, double playerY){
        x += ((playerX + offsetX) - x) / 20;
        y += ((playerY + offsetY) - y) / 20;
    }

    public static double getX() {
        return x;
    }

    public static double getY() {
        return y;
    }
    
}
