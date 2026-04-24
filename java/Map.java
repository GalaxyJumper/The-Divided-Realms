import java.io.File;
import java.util.Scanner;

public class Map {
    Chunk[][] mapData;
    int widthChunks;

    int heightChunks;

    static int currentMapNum = 1;
    static Map[] maps = new Map[]{
        new Map("maps/map1.map", 6, 4)
    };

    public Map(String filePath, int widthChunks, int heightChunks){
        this.heightChunks = heightChunks;
        this.widthChunks = widthChunks;
        mapData = new Chunk[heightChunks][widthChunks];
        loadMap(filePath);
    }
    public void loadMap(String filePath){
        for(int x = 0; x < widthChunks; x++){
            for(int y = 0; y < heightChunks; y++){
                mapData[y][x] = new Chunk(loadChunk(x, y, filePath), x, y);
           }
        }
    }

    // Get tile at any location on the map
    public String getTile(int x, int y){
        if(x > widthChunks * 10 || x < 0) return "";
        if(y > widthChunks * 10 || y < 0) return "";
        return mapData[y / 10][x / 10].getTile(x % 10, y % 10);
    }

    public Chunk getChunk(int xChunks, int yChunks){
        return mapData[yChunks][xChunks];
    }
    
    public int getHeightChunks() {
        return heightChunks;
    }

    public int getWidthChunks() {
        return widthChunks;
    }

    ////////////////////////// STATIC (UTILITY) METHODS //////////////////////////

    public static String[][] loadChunk(int chunkX, int chunkY, String filePath){
        // A single line of text in the file
        String line;
        // A row of chunks represented as an array of Strings
        String[] row;
        // The chunk to load represented as a String
        String[] rawChunk;

        String[][] chunk = new String[10][10]; // The chunk to be loaded (the result)

        // I don't wanna have to deal with methods throwing exceptions
        try {
            Scanner s = new Scanner(new File(filePath));
            // Skip lines until one before the line at y...
            for(int i = 0; i < chunkY; i++){
                s.nextLine();
            }
            // so that the next line will be the one we want.
            line = s.nextLine();
            // Split the line into individual chunks; the regex here reads .split(";}").
            row = line.split(";\\}");

            rawChunk = row[chunkX] // Get the chunk at our desired X
                .substring(1) // Get rid of the first character which is always a {
                    .split(";"); // Split into rows of tiles.
            // rawChunk now reads
            // ["n,n,n,n,n,n,n,n,n", "n,n,n,n,n,n,n,n,n", "n,n,n,n,n,n,n,n,n"...]
            // System.out.println(rawChunk.length);
            for(int i = 0; i < 10; i++){ // For each of the ten rows...
                // Reuse the row variable here to now mean "a row of tiles within a chunk"
                row = rawChunk[i].split(",");
                // Plonk the values into chunk.
                for(int k = 0; k < 10; k++){ // For each tile in a row...
                    chunk[i][k] = row[k];
                }
                
            }
            s.close(); // Make sure no memory leaks sneak their way out
        }catch(Exception e){e.printStackTrace();}
        return chunk;
    }

    public static void setCurrentMap(int mapNum){
        if(mapNum > maps.length){
            mapNum = maps.length;
        }
        if(mapNum <= 0){
            mapNum = 1;
        }
        currentMapNum = mapNum;
    }

    public static Map currentMap(){
        return maps[currentMapNum - 1];
    }
}
