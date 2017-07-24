/*
 * TwitFeedBot is licenced under the The MIT License (MIT)
 * 
 * Copyright (c) 2014–2015 Daloonik daloonik@gmail.com
 * 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package twitfeedbot;
/**
 * GetLine - get random line from file
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


