import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class Dictionary{

  private static final int NUMWORDS = 369892;
  private static final int NUMLINES = 514;
  private static final int NUMFUNCS = 54;
  String[] catalog;
  int [][] table;
  String[] funcs;
  private String lastWord;
  
  //uploads the dictionary and gets everything stored to be checked in the file you are checking
  void uploadDictionary() throws FileNotFoundException{
   
      try{
         File dictionary = new File("dictionary_inorder.txt");
         Scanner words = new Scanner(dictionary);
         updateLocalDict(words);
         updateTableofContents();
         updateLatexFunctions();
         lastWord="1";
         System.out.println("uploaded dictionary and TOC");
      }catch (Exception e){
         System.out.print(e);
      }
        
   } 
   
   //uploads all of the the words into an array
   private void updateLocalDict(Scanner x){
   
      catalog = new String[NUMWORDS];
      int i=0;
      while(x.hasNextLine() && i<NUMWORDS){
         String currentWord = x.next();
         catalog[i]=currentWord;
         i++;
      
      }
   }
   
   //uploads all of the indexes of each word according to length and first letter in the word
   private void updateTableofContents(){
   
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
   
   //uploads all of the latex functions that could potentially be flagged for improper spelling
   private void updateLatexFunctions(){
   
      try{
         File input = new File("latex_functions.txt");
         Scanner toParse = new Scanner (input);
         funcs = new String[NUMFUNCS];
         int i=0;
         while(toParse.hasNextLine() && i<NUMFUNCS){
            String x = toParse.next();
            funcs[i] = x;
            i++;
         }
      }catch(Exception e){
         System.out.println(e);
         System.out.println("latex_functions.txt DID NOT LOAD! Put it in the directory.");
      }
   
   }
      
   //checks to see if your word was spelled correctly
   void readWord(String x, int line, int tokenNum){
   
      if(notLatexFunction(x)){
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
   }
   
   private boolean notLatexFunction(String x){
      
      int i=0;
      while(i<NUMFUNCS){
         if(funcs[i].equals(x)){
            return false;
          }else{
            i++;
          }
       }
       
       return true;
   
   
   }
    
    //finds the range of words in the dictionary that the word you are currently checking based on first letter and length of the word
    private int[] findContents(int len, int ascii){
      
      int[] toReturn = new int[2];
      int i=0;
      while(i<NUMLINES){
         if(ascii==table[i][0]){
            int j=0;
            while(j<21){
               if(len==table[i+j][1]){
                  toReturn[0]= table[i+j][2];
                  toReturn[1]= table[i+j+1][2];
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
   
   private boolean checkSpelling(int lb, int ub, String word){
   
      int localLB = lb;
       while(localLB<=ub){
          if(word.equals(catalog[localLB])){
             return true;
           }
           localLB++;
        }
       
       return false;
   
   }   
               
      
    
      
}