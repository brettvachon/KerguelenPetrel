/*
 * Copyright (c) 2017 daloonik
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package kerguelenpetrel;
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


