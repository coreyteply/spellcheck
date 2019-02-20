import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class ReadFile{

   private File inputFile;

   void readFile(String args[]) throws FileNotFoundException{
   
      if(handleArguments(args)){
         Scanner toParse = new Scanner(inputFile);
         Analyzer.scanInput(toParse);
      }
   }
   
   private static final String USAGE = "Usage: SpellCheck input_tex_file.";

   private boolean handleArguments(String[] args){

      if(args.length != 1){
         System.out.println("Wrong number of command line arguments.");
         System.out.println(USAGE);
         return false;
       }
      
      inputFile = new File(args[0]);
      if(!inputFile.canRead()){
         System.out.println("The file " + args[0] + " cannot be opened for input.");
         System.out.println(USAGE);
         return false;
      }
      
      return true;
   }
   
   

}