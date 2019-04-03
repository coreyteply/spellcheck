import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class LexAnalyzer{

   private Dictionary zeBook;
   private Scanner input; //.txt file that is being read
   private Scanner currentLine; //current line of .txt file
   private String currentToken; //current word
   private int line = 0;
   private int tokenNum=1;
   
    public LexAnalyzer(){
      //starts this class
    }
   
    void scanInput(Scanner inputFile) throws NoSuchElementException{
   
      zeBook = new Dictionary();
      input = inputFile; //make local copy of this instance in this class
      currentToken = null;
      try{
          zeBook.uploadDictionary();
       }catch (Exception e){
          System.out.println(e);
       }
      
      //get to the first token in the file, then start scanning
      while(nullToken()){
         currentLine = new Scanner(input.nextLine());
         line++;
         if(currentLine.hasNext()){
            currentToken = currentLine.next();
            scanNext();
         }
      }
   }

   private void updateNext(){
      
      if(currentLine.hasNext()){
         currentToken = currentLine.next();
         tokenNum++;
         //System.out.println(line);
      }else if(input.hasNextLine() && !(currentLine.hasNext())){
         currentLine = new Scanner(input.nextLine());
         line++;
         tokenNum=0;
         updateNext();
      }else{
         endFile();
      }
   } 
   
   private void scanNext(){
   
      if(fileDone()){
         endFile();
      }else if(canSkipLine()){
         skipLine();
      }else if(canSkipToken()){
         updateNext();
         scanNext();
      }else if(tikz()){
         handleTikz();
      }else if(currentToken.contains("\\") && currentToken.contains("{")){
         parseToken(0);
         updateNext();
         scanNext();
      }else{
         checkWord();
         updateNext();
         scanNext();
      }   

   
   }
   
   private void parseToken(int currentChar){
   
      int tokenLength = currentToken.length();
      int i = currentChar;
      while(i<tokenLength){
         if(currentToken.charAt(i)=='{'){
            i++;
            if(i<tokenLength && currentToken.charAt(i)!='\\'){
               compileWord(tokenLength, i);
               i++;
            }else{
               i++;
            }
          }else{
            i++;
          }
       }
    }
   
   private void compileWord(int length, int currentChar){
   
      int i = currentChar;
      int toPass = 0;
      StringBuilder toBuild = new StringBuilder();
      while(i<length){
         if(currentToken.charAt(i)=='}' || currentToken.charAt(i)=='\\'){
            toPass = i;
            i = length+1;
          }else{
            toBuild.append(currentToken.charAt(i));
            i++;
          }
       }
       String toCheck = toBuild.toString();
       if(back2back(toCheck)){
         createSpace(toCheck);
       }else{
         checkWord2(toCheck);
       }
   
   }
   
   private void checkWord(){
     
     if(back2back(currentToken)){
         createSpace(currentToken);
     }else{
         if(dopedToken()){
            currentToken=cleanToken();
         }
         currentToken = currentToken.toLowerCase();
         if(currentToken.length()!=0 && (int) currentToken.charAt(0) > 96)
            zeBook.readWord(currentToken, line, tokenNum);
      }
   
   }
   
   private boolean back2back(String x){
      
      if(x.contains("}{")){
         return true;
      }else{
         return false;
      }
   
   }
   
   private void createSpace(String x){
   
      int length = x.length();
      int i = 0;
      StringBuilder separateWord = new StringBuilder();
      while(i<length){
         if(x.charAt(i)=='}'){
            String y = separateWord.toString();
            checkWord2(y);
            separateWord = new StringBuilder();
            i=i+2;
         }else{
            separateWord.append(x.charAt(i));
            i++;
         }
      } 
      
      String z = separateWord.toString();
      checkWord2(z); 
   }
   
   private void checkWord2(String x){
   
      String tempToken = currentToken;
      currentToken = x;
      if(dopedToken()){
            currentToken=cleanToken();
      }
      currentToken = currentToken.toLowerCase();
      if(currentToken.length()!=0 && (int) currentToken.charAt(0) > 96)
         zeBook.readWord(currentToken, line, tokenNum);
         
      currentToken = tempToken;
   
   }
   
   private void handleTikz(){
   
      while(!(currentToken.equals("\\end{tikzpicture}"))){
         currentLine = new Scanner(input.nextLine());
         line++;
         tokenNum=0;
         updateNext();
      }
      skipLine();
   
   }

   private void skipLine(){
  
         currentLine = new Scanner(input.nextLine());
         line++;
         tokenNum=0;
         updateNext();
         scanNext();
  
  }
  
  private boolean canSkipToken(){
  
      if((currentToken.contains("\\") && !(currentToken.contains("{"))) || currentToken.contains("@")
            || currentToken.contains("$")){
         return true;
       }else{
         return false;
       }
  
  }
  
  private boolean tikz(){
      
      if(currentToken.contains("tikzpicture")){
         return true;
      }else{
         return false;
      }
      
  }
  
  private boolean canSkipLine(){
  
      if(currentToken.contains("%") || currentToken.contains("tabular") || currentToken.contains("usepackage")){
         return true;
      }else{
         return false;
      }
  
  }
   
   private boolean dopedToken(){
   
      if(currentToken.contains(",") || currentToken.contains("(") || currentToken.contains(")") || currentToken.contains("'") ||
               currentToken.contains("<") || currentToken.contains(">")  || currentToken.contains("=") ||
               currentToken.contains(".") || currentToken.contains("?") || currentToken.contains("!") || 
               currentToken.contains("{") || currentToken.contains("\\") || currentToken.contains(":") ||
               currentToken.contains("-") || currentToken.contains("}")){
         return true;
      }else{
         return false;
      }
      
   }
   
   private boolean dopedChar(char x){
   
      if(x==',' || x=='(' || x==')' || x=='<' || x=='>' || x=='=' || x=='.' || x=='?' || x=='!' || x=='}' || x=='\\' || x=='/' ||
                  x==':' || x=='-' || x=='{' || x=='\''){
         return true;
      }
      
      return false;
   
   }   
   
   //cleans up a token if it has any sort of puncuation on it
   private String cleanToken(){
   
      int length = currentToken.length();
      int count=0;
      StringBuilder newToken = new StringBuilder();
      while(count<length){
         if(dopedChar(currentToken.charAt(count))){
            count++;
         }else{
            newToken.append(currentToken.charAt(count));
            count++;
         }
      }
      
      return newToken.toString();
   }

   
   private boolean nullToken(){
      if(currentToken==null){
         return true;
      }
      return false;
   }
   
   private void endFile(){
      //System.out.println("you are done reading dis file");
   }      
   
   //checks to see if the file is done
   private boolean fileDone(){
      if(currentToken.equals("\\end{document}")){
         return true;
      }else{
         return false;
      }
   } 



}