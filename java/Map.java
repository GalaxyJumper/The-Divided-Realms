import java.io.File;
import java.util.Scanner;

public class Map {
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
                    System.out.println(chunk[i][k]);
                }
                
            }
            s.close(); // Make sure no memory leaks sneak their way out
        }catch(Exception e){e.printStackTrace();}
        return chunk;
    }
}
