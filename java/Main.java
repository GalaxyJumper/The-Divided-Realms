
import java.util.ArrayList;
import java.util.stream.IntStream;

import cooking.ItemData;
import game.GameLoop;
import gui.Renderer;
import maptool.MapTool;

import com.jogamp.opengl.GL2;
public class Main {
    public static void main(String[] args) { 
        if(GameLoop.mapToolMode){
            MapTool.start();
        } else {
            GameLoop.start();
        }
    }
    public static String repeat(String s, int n){
        return s + ((n > 1)? repeat(s, n - 1) : "");
    }
    public static void square(int a, String s){
        System.out.println(repeat(repeat(s, a) + "\n", a));
    }
    public static boolean palindrome(String s){
        return (s.charAt(0) == s.charAt(s.length() - 1)) && ((s.length() <= 1)? true : palindrome(s.substring(1, s.length() - 1)));
    }
    public static int findMin(int[] arr, int start, int end){
        if(start == end){
            return arr[start];
        }
        else return findMin(arr, (arr[start] > arr[end])? (start + 1) : (start), (arr[end] > arr[start])? (end - 1) : end);
    }
    public static int count7(int num){
        return (num > 10) ?
            ((num % 10 == 7)? 1 : 0) + count7((int)Math.floor((float)num / 10.0)) :
            ((num % 10 == 7)? 1 : 0);
    }
    public static String cleanString(String s){
        System.out.println(s);
        if(s.length() == 1 || s.substring(0, 1).equals(s.substring(1, 2))) {
            return s.substring(1);
        } else {
            return s.substring(0, 1) + cleanString(s.substring(1));
        }
    }

}

