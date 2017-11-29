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

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.Trends;
import twitter4j.User;

import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.jeremybrooks.knicker.KnickerException;
import net.jeremybrooks.knicker.WordApi;
import net.jeremybrooks.knicker.WordsApi;

@SuppressWarnings("serial")

public class BotherSomeoneServlet extends HttpServlet
 {
 public static final long cursor = -1;

 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {

   StringBuilder builder = new StringBuilder();
   User victim = null;  

   long[] friendIDs, victimIDs;
   Random r = new Random();   

   resp.setContentType("text/plain; charset=UTF-8");
   try 
     {
     //Get the Twitter object
     Twitter twit = TwitterFactory.getSingleton();

     //Find a friend of a follower to bother
     friendIDs = twit.getFollowersIDs(twit.getId(), cursor).getIDs();
     
     if(friendIDs.length == 0)
        {
        resp.getWriter().println("Cannot find any followers to bother \n");
        return;
        }
     
     //Load the potential victim IDs
     victimIDs = twit.getFollowersIDs(friendIDs[r.nextInt(friendIDs.length)], cursor).getIDs();
 
     if(victimIDs.length == 0)
        {
         resp.getWriter().println("Cannot find any followers to bother \n");
         return;
        }
       
       //Get a user to bother
       victim = twit.showUser(victimIDs[r.nextInt(victimIDs.length)]);
       
       //Mention the user
       builder.append("@" + victim.getScreenName() + " ");
       
       //Append Wordnik example sentence
       Properties p = new Properties();
       InputStream in = BotherSomeoneServlet.class.getResourceAsStream("wordnik.properties");
       p.load(in);
       System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
       builder.append(WordApi.topExample(WordsApi.randomWord().getWord()).getText());
       
       /* Tweets are maximum 280 characters, so trim our sentence appropriately */
       if(builder.length() > 280) 
          {
          if(builder.lastIndexOf(";",220) > 0)
             builder.setLength(builder.lastIndexOf(";",220)); 
          else if(builder.lastIndexOf(":",220) > 0)
             builder.setLength(builder.lastIndexOf(":", 220));  
          else if(builder.lastIndexOf(",",220) > 0)
             builder.setLength(builder.lastIndexOf(",", 220));
          else builder.setLength(220);
          }
         
       //Append some global trends
       Trends t = twit.getPlaceTrends(1);  
       builder.append(" ");
       builder.append(t.getTrends()[r.nextInt(t.getTrends().length)].getName());
         
       if(builder.length() > 280)
          builder.setLength(280); //Tweets are limited to 280 characters

       twit.updateStatus(builder.toString());
       
       resp.getWriter().println("Tweet posted: "+builder.toString() +"\n"); 
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
   catch(Exception e)
    {
     resp.getWriter().println("Problem! \n");
     e.printStackTrace(resp.getWriter());
     }
   }
 }


