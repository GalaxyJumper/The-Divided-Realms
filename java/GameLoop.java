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
                    // Input -> Update -> Render
                    // game.update(inputState);


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
}
