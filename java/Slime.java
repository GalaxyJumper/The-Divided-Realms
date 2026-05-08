public class Slime extends Enemy{
    private double[] pathfindingTarget = new double[2];
    private int lastTargetUpdate = (int) System.currentTimeMillis() - 5000;
    private int timeTilNextTargetUpdate = 4500;
    public Slime(double x, double y){
        super(x, y);
    }

    @Override
    public void update() {
        xPos += xVel;
        yPos += yVel;
        if((int) System.currentTimeMillis() - lastTargetUpdate > timeTilNextTargetUpdate){
            lastTargetUpdate = (int) System.currentTimeMillis();
            timeTilNextTargetUpdate = (int)(Math.abs(Math.random()) * 10000.0 + 2000.0);
            pathfindingTarget = new double[] {
                xPos + Math.random() * 6 - 3,
                yPos + Math.random() * 6 - 3
            };
        }
        xVel = GameLoop.clamp((pathfindingTarget[0] - xPos) * 0.5, -0.06, 0.06);
        yVel = GameLoop.clamp((pathfindingTarget[1] - yPos) * 0.5, -0.06, 0.06);
        pathfindingTarget[0] += (Math.random() * 0.024) - 0.012;
        pathfindingTarget[1] += (Math.random() * 0.024) - 0.012;
        if(Math.sqrt(Math.pow(xPos - Player.getxPos(), 2) + Math.pow(yPos - Player.getyPos(), 2)) < 4){
            pathfindingTarget[0] = Player.getxPos();
            pathfindingTarget[1] = Player.getyPos();
        }

    }
}
