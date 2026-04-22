public class Chunk {
    String[][] data;
    int x;
    int y;
    public Chunk(String[][] data, int x, int y){
        this.data = data;
        this.x = x;
        this.y = y;
    }
    public String[][] getData(){
        return data;
    }
    public String getTile(int x, int y){
        return data[y][x];
    }
}