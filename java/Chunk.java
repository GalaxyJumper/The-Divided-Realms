public class Chunk {
    String[][] tileData;
    String[][] cliffData;
    int[][]    heightData;
    int x;
    int y;
    public Chunk(String[][] tileData, String[][] cliffData, int[][] heightData, int x, int y){
        this.tileData = tileData;
        this.heightData = heightData;
        this.cliffData = cliffData;
        this.x = x;
        this.y = y;
    }
    public String[][] getTileData(){
        return tileData;
    }

    public String getTileAt(int x, int y){
        if(x >= 10 || x < 0){
            x = 0;
        }
        if(y >= 10 || y < 0){
            y = 0;
        }
        return tileData[y][x];
    }

    public String[][] getCliffData(){
        return cliffData;
    }
    public String getCliffAt(int x, int y){
        if(x >= 10 || x < 0){
            x = 0;
        }
        if(y >= 10 || y < 0){
            y = 0;
        }
        return cliffData[y][x];
    }

    public int[][] getHeightData(){
        return heightData;
    }
    public int getHeightAt(int x, int y){
        if(x >= 10 || x < 0){
            x = 0;
        }
        if(y >= 10 || y < 0){
            y = 0;
        }
        return heightData[y][x];
    }
    public int getXChunks(){
        return x;
    }
    
    public int getYChunks(){
        return y;
    }
}
