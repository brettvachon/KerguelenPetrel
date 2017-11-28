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

import java.io.IOException;
import java.io.InputStream;
import java.io.FileNotFoundException;

import com.sun.syndication.io.FeedException;

import java.util.Properties;
import java.util.Random;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.WordsApi;

@SuppressWarnings("serial")

public class UpdateStatusServlet extends HttpServlet 
 {
 public static final String feedsFile = "WEB-INF/StaticFiles/feeds";
 
 public static final String[] separator = new String[] { "?","!",",","-","?!" };
 
 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   Random r = new Random();
   
   resp.setContentType("text/plain; charset=UTF-8");
   
   try 
     {
     resp.getWriter().println("Updating status...");

     StringBuilder builder = new StringBuilder(140);   //Tweets are 140 characters
     //Append feed title
     GetFeed feed = new GetFeed(feedsFile); 
     builder.append(feed.title());
     builder.append(separator[(r.nextInt(separator.length))] + " ");
     
     //Append Wordnik example sentence
     Properties p = new Properties();
     InputStream in = UpdateStatusServlet.class.getResourceAsStream("wordnik.properties");
     p.load(in);
     System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
     
     builder.append(WordApi.topExample(WordsApi.randomWord().getWord()).getText());
    
     if(builder.length() > 140) //Tweets are maximum 140 characters
        {
        if(builder.lastIndexOf(";",110) > 0)
           builder.setLength(builder.lastIndexOf(";",110)); 
        else if(builder.lastIndexOf(":",110) > 0)
           builder.setLength(builder.lastIndexOf(":", 110));  
        else if(builder.lastIndexOf(",",110) > 0)
           builder.setLength(builder.lastIndexOf(",", 110));
        else builder.setLength(110);
        }
     //Append a Global trend
     Twitter twit = TwitterFactory.getSingleton();
     builder.append(" "+twit.getPlaceTrends(1).getTrends()[r.nextInt(twit.getPlaceTrends(1).getTrends().length)].getName());
     
     // Make up a trend by combining two words
     builder.append(" #" + WordsApi.randomWord().getWord() + WordsApi.randomWord().getWord());
          
     if(builder.length() > 140)
        builder.setLength(140); //Tweets are limited to 140 characters
     
     //Update the Twitter status
     
     twit.updateStatus(builder.toString());
     resp.getWriter().println("Tweet posted: "+ builder.toString());
     }
   catch(FileNotFoundException e)
     {
     resp.getWriter().println("Input file(s) not found \n");
     e.printStackTrace(resp.getWriter());
     }
   catch(TwitterException e)
      {
      resp.getWriter().println("Problem with Twitter \n");
      e.printStackTrace(resp.getWriter());
      }
    catch(KnickerException e)
      {
     resp.getWriter().println("Problem with Wordnik \n");
     e.printStackTrace(resp.getWriter());
      }
   catch(FeedException e)
      {
      resp.getWriter().println("Problem with RSS Feed \n");
      e.printStackTrace(resp.getWriter());
      }
   
   catch(Exception e)
      {
      resp.getWriter().println("Problem! \n");
      e.printStackTrace(resp.getWriter());
      }
    }
}
