package baconbot_daloonik;
/**
 * GetLine - get random line from file
 * This code is open source, use at your own risk, blah blah blah...
 * Made in 2014
 */

import java.util.Random;
import java.util.Scanner;

import java.io.File;
import java.io.FileNotFoundException;

public class GetLine
 {
 private String file = "";
 /**
  * Returns a random line from a file of unknown file length
  * @return randline Random line from file
  */
 public String line() throws FileNotFoundException
   {
   int numlines = 1;
   String buf = "";
   String randline = "";
   Random rand = new Random();
  
   Scanner input = new Scanner(new File(file), "UTF-8");
   while (input.hasNextLine())
        {  
         buf = input.nextLine();
         if ((rand.nextInt()%numlines) == 0)
            randline = buf;  //chance of saving line n 1/n
         numlines++;
         }
   input.close();
   return randline;
    
   }
 /** Sets local filename variable
  * 
  * @param filename file with sentences
  */
 public GetLine(String filename)
   {
    this.file = filename;
   }
   
 }


