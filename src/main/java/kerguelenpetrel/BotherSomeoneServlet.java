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
import java.io.PrintWriter;
import java.io.FileNotFoundException;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.StatusUpdate;

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
 public static final Random r = new Random();

 @Override
 public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
   {
   StringBuilder builder = new StringBuilder();
   PrintWriter out = resp.getWriter();
   long[] friendIDs, victimIDs;

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
       
     //Write to our victim
     String victim = twit.showUser(victimIDs[r.nextInt(victimIDs.length)]).getScreenName();
     builder.append("@" + victim + " ");

     //Append Wordnik example sentence as content
     Properties p = new Properties();
     InputStream in = UpdateStatusServlet.class.getResourceAsStream("wordnik.properties");
     p.load(in);
     System.setProperty("WORDNIK_API_KEY", p.getProperty("WORDNIK_API_KEY"));
     builder.append(WordApi.topExample(WordsApi.randomWord().getWord()).getText());
     

     if(builder.length() > 280)
          builder.setLength(280); //Tweets are limited to 280 characters
       
     //Set the status
     StatusUpdate status = new StatusUpdate(builder.toString());
       
     //Post the status
     twit.updateStatus(status);
     resp.getWriter().println("Tweet posted: "+ status.getStatus());
     } 

   catch(TwitterException e)
     {
     out.println("Problem with Twitter \n");
     e.printStackTrace(resp.getWriter());
     }
   catch(KnickerException e)
      {
     out.println("Problem with Wordnik \n");
     e.printStackTrace(resp.getWriter());
      }
   catch(FileNotFoundException e)
      {
      out.println("Wordnik property file not found \n");
      }
   finally 
      {
      out.close();  // Always close the output writer
      } 
   }
 }