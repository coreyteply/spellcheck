import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class Dictionary{

  private static final int NUMWORDS = 369892;
  private static final int NUMLINES = 514;
  public static String[] catalog;
  public static int [][] table;
  private static String lastWord;
  
  public static void uploadDictionary() throws FileNotFoundException{
   
      try{
         File dictionary = new File("dictionary_inorder.txt");
         Scanner words = new Scanner(dictionary);
         updateLocalDict(words);
         updateTableofContents();
         lastWord="1";
         System.out.println("uploaded dictionary and TOC");
      }catch (Exception e){
         System.out.print(e);
      }
        
   } 
   
   private static void updateLocalDict(Scanner x){
   
      catalog = new String[NUMWORDS];
      int i=0;
      while(x.hasNextLine() && i<NUMWORDS){
         String currentWord = x.next();
         catalog[i]=currentWord;
         i++;
      
      }
   }
   
   private static void updateTableofContents(){
   
      try{
         File input = new File("finalTOC.txt");
         Scanner toParse = new Scanner(input);
         table = new int[NUMLINES][3];
         int i=0;
         while(toParse.hasNextLine()){
            Scanner currentLine = new Scanner(toParse.nextLine());
            table[i][0]=Integer.parseInt(currentLine.next());
            table[i][1]=Integer.parseInt(currentLine.next());
            table[i][2]=Integer.parseInt(currentLine.next());
            i++;
         }
      }catch (Exception e){
         System.out.println(e);
         System.out.println("tableofcontents_final.txt DID NOT LOAD! Put it in the directory.");
      } 
   
   }
      
   
   public static void readWord(String x, int line, int tokenNum){
   
      //System.out.println(x);
      if(x.equals(lastWord)){
         System.out.println("Repeated word: \""+x+"\" at line: "+line+" at token number: "+tokenNum);
      }else{
         lastWord=x;
         int wordLen = x.length();
         int firstLetter = (int) x.charAt(0);
         int[] bounds1 = findContents(wordLen, firstLetter);
         int lowerBound1 = bounds1[0];
         int upperBound1 = bounds1[1];
         if(upperBound1!=-1){
             if(!(checkSpelling(lowerBound1, upperBound1, x))){
                System.out.println("MISPELLED: \""+x+"\" at line: "+line+" at token number: "+tokenNum);
              }
         }
      }   
   }
    
    private static int[] findContents(int len, int ascii){
      
      int[] toReturn = new int[2];
      int i=0;
      while(i<NUMLINES){
         if(ascii==table[i][0]){
            int j=0;
            while(j<21){
               if(len==table[i+j][1]){
                  toReturn[0]= table[i+j][2];
                  toReturn[1]= table[i+j+1][2]; //make condition for last letter for z
                 // System.out.println("contents: "+i+" "+j);
                  //System.out.println("index: "+table[i+j][2]);
                  return toReturn;
               }else{
                  j++;
               }
            }
         }else{
            i++;
         }   
      }
      return toReturn;
   }
   
   private static boolean checkSpelling(int lb, int ub, String word){
   
      int localLB = lb;
       while(localLB<=ub){
         //System.out.println("in checkspell: "+catalog[index[localLB]]);
          if(word.equals(catalog[localLB])){
             return true;
           }
           localLB++;
        }
       
       return false;
   
   }   
               
      
    
      
}