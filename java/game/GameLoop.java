package game;
import java.util.ArrayList;

import java.awt.event.MouseEvent;

import game.Input;
import game.Player;
import gui.Renderer;
import enemies.Bee;
import enemies.Enemy;
import enemies.Slime;
import gui.Camera;

public class GameLoop {
    private static boolean running = false;
    private static int framesLastSecond = 0;
    public static int framesThisSecond = 0;
    private static long lastSecondTime = System.nanoTime();
    private static long now;
    public static Input input;
    private static ArrayList<Enemy> enemies = new ArrayList<Enemy>();
    public static void start () {
        Thread thread = new Thread() {

            public void run (){
                running = true;
                input = new Input();
                Renderer.init();
                
                for(int i = 0; i < 8; i++){
                    enemies.add(new Slime(5 + Math.random() * 10, 5 + Math.random() * 10));
                }
                for(int i = 0; i < 10; i++){
                    enemies.add(new Bee(5 + Math.random() * 10, 5 + Math.random() * 10));
                }

                //new Sounds();
                //Sounds.playSound("Countryside");
                while(running) {
                    now = System.nanoTime();
                                             // 1 second
                    if(now > lastSecondTime + 1000000000){
                        lastSecondTime = now;
                        framesLastSecond = framesThisSecond;
                        framesThisSecond = 0;
                        //System.out.println(framesLastSecond);
                    }


                    Camera.update(Player.getxPos(), Player.getyPos());
                    for(int i = 0; i < enemies.size(); i++){
                        if(rectangleCollide(enemies.get(i).getHitbox(), Player.getAttackHitbox())){
                            double distanceToTarget = GameLoop.dist(enemies.get(i).getxPos(), enemies.get(i).getyPos(), Player.getxPos(), Player.getyPos());
                            // print player attack hitbox and enemy hitbox
                            if((int)System.currentTimeMillis() - Player.getLastAttack() < 20){
                                enemies.get(i).applyDamage(20);
                                enemies.get(i).applyVelocity(0.2 * (enemies.get(i).getxPos() - Player.getxPos()) / distanceToTarget, 0.2 * (enemies.get(i).getyPos() - Player.getyPos()) / distanceToTarget);
                            }
                            System.out.println("Hit! Enemy: " +  enemies.get(i).getClass().getSimpleName() + " Health: " + enemies.get(i).getHealth());
                        }
                        
                        enemies.get(i).update();
                    }
                    Renderer.renderGame();
                    Player.update(input);
                    framesThisSecond ++;
                }
            }
        };
        thread.setName("GameLoop");
        thread.start();
        // rectangle collisions:

        // DETECTION:
        //
        // graph collisions:

        // for every point:
        //  if(point < mx + b and is within bounds){
        //      everything move by   
        //  }
    }
    public static ArrayList<Enemy> getEnemies(){
        return enemies;
    }
    public static boolean rectangleCollide(double[] a, double[] b) {
        return a[0] < b[0] + b[2]
            && a[0] + a[2] > b[0]
            && a[1] < b[1] + b[3]
            && a[1] + a[3] > b[1];
    }
    public static void handleMouseClick(MouseEvent e){
        Player.handleMouseClick(e);
    }
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
    public static double dist(double x1, double y1, double x2, double y2){
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
}
