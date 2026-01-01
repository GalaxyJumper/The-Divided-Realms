public class GameLoop {
    private static boolean running = false;
    public static void start () {
        Thread thread = new Thread() {
            public void run (){
                running = true;
                while(running) {
                    // Input -> Update -> Render
                    // game.update(inputState);

                    Renderer.renderGame();
                    System.out.println("Frame gened");
                }
            }
        };
        thread.setName("GameLoop");
        thread.start();
    }
}
