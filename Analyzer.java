import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class Analyzer{

   private Dictionary zeBook;
   private Scanner input; //.txt file that is being read
   private Scanner currentLine; //current line of .txt file
   private String currentToken; //current word
   private int leftBC=0; //keeps track of how many left brakets you have iterated through
   private int currChar=0;
   private int currLength=0;
   private int dollaCount = 0;
   private int mathIndex = 0; //1=return to handleMath1(), etc...
   private int line = 1;
   private int tokenNum=1;
   
   void scanInput(Scanner inputFile) throws NoSuchElementException{
   
      zeBook = new Dictionary()
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
         //System.out.println(currentToken);
         tokenNum++;
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
      }else if(textMod()){
         //leftBC++;
         currLength = currentToken.length();
         handleTextMod();
      }else if(currentToken.contains("\\[")){
         updateNext();
         mathIndex=3;
         handleMath3();
      }else if(canSkipLine()){
         skipLine();
      }else if(currentToken.contains("begin{tikzpicture}")){
         handleTikz();
      }else if(currentToken.charAt(0)=='\\'){
         updateNext();
         scanNext();
      }else if(currentToken.contains("$")){
         mathIndex=1;
         handleMath1();
      }else{
         if(dopedToken()){
            currentToken=cleanToken();
         }
         currentToken = currentToken.toLowerCase();
         if(currentToken.length()!=0 && (int) currentToken.charAt(0) > 96)
            zeBook.readWord(currentToken, line, tokenNum);
         updateNext();
         scanNext();
      }   
      
   
   }
         
   
   
   //for anything that has a text modification on it, like making it boldface for ex.
   private void handleTextMod(){
     
     try{
         if(currentToken.charAt(5+currChar)=='{' && (currentToken.charAt(3+currChar)=='x' || 
                           currentToken.charAt(currChar+1)=='H' || currentToken.charAt(currChar+1)=='h')){ //for \text{, \huge{, \Huge{
            currChar= 6+currChar;
            iterateTextMod();
         }else if(currentToken.charAt(6+currChar)=='{' && (currentToken.charAt(1+currChar)=='l' || currentToken.charAt(1+currChar)=='L')){
            currChar=7+currChar; //for \large{ and \Large{
            iterateTextMod();
         }else if(currentToken.charAt(7+currChar)=='{' && (currentToken.charAt(5+currChar)=='b' || currentToken.charAt(5+currChar)=='i')){
            currChar=8+currChar; //for \textbf{ and \textit{
            iterateTextMod();
         }else if(currentToken.charAt(10+currChar)=='{' && currentToken.charAt(1+currChar)=='u'){ //for underline{
            currChar=11+currChar;
            iterateTextMod();
         }else if(currentToken.charAt(11+currChar)=='{' && currentToken.charAt(1+currChar)=='n'){
            currChar=12+currChar; //for \normalsize{
            iterateTextMod();
         }
      }catch (Exception e){
         System.out.println(e);
         System.out.println(currentToken+" "+ currChar+" "+ currLength);
         iterateTextMod();
      }
      
      
   }
   
   //read the text in the text mod once you reach that point in the token
   private void readTextMod(){
      
      StringBuilder currWord = new StringBuilder();
      while(currChar<currLength){
         if(dopedChar(currentToken.charAt(currChar))){
            currChar++;
          }else{
            currWord.append(currentToken.charAt(currChar));
            currChar++;
          }
       }
       
        String toCheck = currWord.toString();
        currChar=0;
        currLength=0;
        currentToken = toCheck;
       if(mathIndex==3){
         handleMath3();
       }else{
         scanNext();
      }   
   }
            
   
   //see if the next part of the command involves more text mods
   private void iterateTextMod(){
      
      if(currentToken.charAt(currChar)=='\\' && currChar<currLength){
         //leftBC++;
         handleTextMod();
      }else{
         readTextMod();
      }
   }
      
   
   private boolean textMod(){
      if(currentToken.contains("textbf{") || currentToken.contains("textit{") || currentToken.contains("underline{") 
            || currentToken.contains("text{") || currentToken.contains("large{") || currentToken.contains("huge{") || 
               currentToken.contains("Large{") || currentToken.contains("normalsize{")){
               return true;
      }
      
      return false;
  }
  
  private void handleMath1(){
      
      currLength=currentToken.length();
      int i=0;
      while(i<currLength){
         if(currentToken.charAt(i)=='$'){
            dollaCount++;
            i++;
         }else{
            i++;
         }
     }
     currLength=0;
     if(dollaCount%2==0){
      mathIndex=0;
      dollaCount=0;
      updateNext();
      scanNext();
     }else{
      updateNext();
      handleMath2();
     }
      
  }
  
  private void handleMath2(){
  
   if(currentToken.contains("$")){
      handleMath1();
   }else{
      updateNext();
      handleMath2();
   }
  
  }
  
  private void handleMath3(){
  
   if(currentToken.equals("\\]")){
      updateNext();
      mathIndex=0;
      scanNext();
    }else if(textMod()){
      leftBC++;
      handleTextMod();
    }else if(leftBC>0){
      if(currentToken.contains("}")){
         leftBC=0;
      }
      if(dopedToken()){
            currentToken=cleanToken();
         }
         currentToken = currentToken.toLowerCase();
         if(currentToken.length()!=0 && (int) currentToken.charAt(0) > 96)
            Dictionary.readWord(currentToken, line, tokenNum);
         updateNext();
         handleMath3();
    }else{
      updateNext();
      handleMath3();
    }
  }  
  
  private void handleTikz(){
   
      if(currentToken.equals("\\end{tikzpicture}")){
         updateNext();
         scanNext();
      }else{
         updateNext();
         handleTikz();
      }
  }
  
  
  private boolean canSkipLine(){
  
     if(currentToken.contains("includegraphics") || currentToken.charAt(0)=='%'){
         return true;
     }   
     return false;
  
  }
  
  private void skipLine(){
  
         currentLine = new Scanner(input.nextLine());
         line++;
         tokenNum=0;
         updateNext();
         scanNext();
  
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
                  x==':' || x=='-' || x=='{'){
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