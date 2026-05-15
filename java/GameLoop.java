public class GameLoop {
    private static boolean running = false;
    private static int framesLastSecond = 0;
    public static int framesThisSecond = 0;
    private static long lastSecondTime = System.nanoTime();
    private static long now;
    public static Input input;
    public static void start () {

        Thread thread = new Thread() {

            public void run (){
                running = true;
                input = new Input();
                Renderer.init();
                //new Sounds();
                //Sounds.playSound("Countryside");
                while(running) {
                    now = System.nanoTime();
                                             // 1 second
                    if(now > lastSecondTime + 1000000000){
                        lastSecondTime = now;
                        framesLastSecond = framesThisSecond;
                        framesThisSecond = 0;
                        System.out.println(framesLastSecond);
                    }


                    Camera.update(Player.getxPos(), Player.getyPos());
                    Renderer.renderGame();
                    Player.update(input);

                    framesThisSecond ++;
                }
            }
        };
        thread.setName("GameLoop");
        thread.start();
    }
    public static double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }
    public static double dist(double x1, double y1, double x2, double y2){
        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }
}
