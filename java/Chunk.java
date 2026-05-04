import javax.swing.event.SwingPropertyChangeSupport;

public class Chunk {
    String[][] tileData;
    String[][] cliffDirData;
    int[][] cliffHeightData;
    int[][] heightData;
    int x;
    int y;
    public Chunk(String[][] tileData, String[][] cliffData, int[][] heightData, int x, int y){
        this.tileData = tileData;
        this.heightData = heightData;
        this.cliffDirData = new String[cliffData.length][cliffData[0].length];
        this.cliffHeightData = new int[cliffData.length][cliffData[0].length];
        for(int i = 0; i < cliffData.length; i++){
            for(int k = 0; k < cliffData[i].length; k++){
                cliffDirData[i][k] = cliffData[i][k].split("\\.")[1];
                cliffHeightData[i][k] = Integer.parseInt(cliffData[i][k].split("\\.")[0]);
            }
        }
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

    public String[][] getCliffDirData(){
        return cliffDirData;
    }
    public String getCliffDirAt(int x, int y){
        if(x >= 10 || x < 0){
            x = 0;
        }
        if(y >= 10 || y < 0){
            y = 0;
        }
        return cliffDirData[y][x];
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
