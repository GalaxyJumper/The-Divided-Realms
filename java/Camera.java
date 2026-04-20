public class Camera {
    static double x = 0.6;
    static double y = 2.0;
    static double offsetX = 0.05;
    static double offsetY = 3.0;

    static public void update(double playerX, double playerY){
        x += ((playerX + offsetX) - x) / 25;
        y += ((playerY + offsetY) - y) / 25;
    }

    public static double getX() {
        return x;
    }

    public static double getY() {
        return y;
    }
    
}
