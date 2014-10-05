package daloonik_at_gmail_dot_com.twitterbot;
/**
 * GetLine - get random line from file
 * This code is open source, use at your own risk, blah blah blah...
 * Made in 2014
 */

import java.util.Random;
import java.util.Scanner;

import java.io.FileReader;
import java.io.FileNotFoundException;

public class GetLine
 {
 private String file = "";
 /**
  * Returns a random line from a file of unknown file length
  * @return randline Random line from file
  */
 public String line()
   {
   int numlines = 1;
   String buf = "";
   String randline = "";
   Random rand = new Random();
  
   try (Scanner input = new Scanner(new FileReader(file)))
     {
     while (input.hasNextLine())
        {  
         buf = input.nextLine();
         if ((rand.nextInt()%numlines) == 0)
            randline = buf;  //chance of saving line n 1/n
         numlines++;
         }
      }
   catch (FileNotFoundException e) 
      { 
      e.printStackTrace();
      System.out.println((System.lineSeparator())+"File "+file +" not found!");
      }
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


