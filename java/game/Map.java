package game;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Map {
    public static final int HEIGHT = 5;



    Chunk[][] mapData;
    public int widthChunks;

    public int heightChunks;

    static int currentMapNum = 1;
    static Map[] maps = new Map[]{
        new Map("maps/map1", 6, 4)
    };

    public Map(String folderPath, int widthChunks, int heightChunks){
        this.heightChunks = heightChunks;
        this.widthChunks = widthChunks;
        mapData = new Chunk[heightChunks][widthChunks];
        loadMap(folderPath);
    }
    public void loadMap(String filePath){
        for(int x = 0; x < widthChunks; x++){
            for(int y = 0; y < heightChunks; y++){
                mapData[y][x] = new Chunk(loadTileData(x, y, filePath + "/tiles.map"), loadCliffData(x, y, filePath + "/cliffs.map"), loadElevData(x, y, filePath + "/height.map"), x, y);
           }
        }
    }

    // Get tile at any location on the map
    public String getTile(int x, int y){
        if(x >= widthChunks * 10 || x < 0) return "";
        if(y >= heightChunks * 10 || y < 0) return "";
        return mapData[y / 10][x / 10].getTileAt(x % 10, y % 10);
    } 
        // Get tile at any location on the map
    public int getHeight(int x, int y){
        if(x >= widthChunks * 10 || x < 0) return 0;
        if(y >= heightChunks * 10 || y < 0) return 0;
        return mapData[y / 10][x / 10].getHeightAt(x % 10, y % 10);
    } 
        // Get tile at any location on the map
    public int getCliffHeight(int x, int y){
        if(x >= widthChunks * 10 || x < 0) return 0;
        if(y >= heightChunks * 10 || y < 0) return 0;
        return mapData[y / 10][x / 10].getCliffHeightAt(x % 10, y % 10);
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
    public void editTile(int chunkX, int chunkY, int tileX, int tileY, String newValue){
        mapData[chunkY][chunkX].editTile(tileX, tileY, newValue);
    }

    ////////////////////////// STATIC (UTILITY) METHODS //////////////////////////

    public static String[][] loadTileData(int chunkX, int chunkY, String filePath){
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

    public static String[][] loadCliffData(int chunkX, int chunkY, String filePath){
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

    public static int[][] loadElevData(int chunkX, int chunkY, String filePath){
        // A single line of text in the file
        String line;
        // A row of chunks represented as an array of Strings
        String[] row;
        // The chunk to load represented as a String
        String[] rawChunk;

        int[][] chunk = new int[10][10]; // The chunk to be loaded (the result)

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
                    chunk[i][k] = Integer.parseInt(row[k]);
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
    public void saveMap(){
        System.out.println("Saving map... please wait");
        File outputCliffsFile = new File("maps/cliffs.map");
        File outputHeightFile = new File("maps/height.map");
        File outputEnvFile = new File("maps/env.map");
        File outputTileFile = new File("maps/tiles.map");
        FileWriter fwc;
        FileWriter fwh;
        FileWriter fwe;
        FileWriter fwt;

        try {
            fwc = new FileWriter(outputCliffsFile, false);
            fwc.write("");
            for(int y = 0; y < Map.currentMap().heightChunks; y++){
                for(int x = 0; x < Map.currentMap().widthChunks; x++){
                    fwc.append(Map.currentMap().getChunk(x, y).getCliffDataAsString());
                }
                fwc.write("\n");
            }
            fwc.close();

            fwh = new FileWriter(outputHeightFile, false);
            fwh.write("");
            for(int y = 0; y < Map.currentMap().heightChunks; y++){
                for(int x = 0; x < Map.currentMap().widthChunks; x++){
                    fwh.append(Map.currentMap().getChunk(x, y).getHeightDataAsString());
                }
                fwh.write("\n");
            }
            fwh.close();

            fwe = new FileWriter(outputEnvFile, false);
            fwe.write("");
            for(int y = 0; y < Map.currentMap().heightChunks; y++){
                for(int x = 0; x < Map.currentMap().widthChunks; x++){
                    // fwe.append(Map.currentMap().getChunk(x, y).getEnvDataAsString());
                }
                fwe.write("\n");
            }
            fwe.close();

            fwt = new FileWriter(outputTileFile, false);
            fwt.write("");
            for(int y = 0; y < Map.currentMap().heightChunks; y++){
                for(int x = 0; x < Map.currentMap().widthChunks; x++){
                    fwt.append(Map.currentMap().getChunk(x, y).getTileDataAsString());
                }
                fwt.write("\n");
            }
            fwt.close();
        } catch(IOException e){e.printStackTrace();}
        System.out.println("Map saved!");
    }
}
