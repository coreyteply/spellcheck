import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class SpellCheck{

   public static void main(String[] args) throws FileNotFoundException{
   
     ReadFile toRead = new ReadFile();
     toRead.readFile(args);
     System.gc();

   
   }


}